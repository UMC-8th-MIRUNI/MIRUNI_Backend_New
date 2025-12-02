package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.AiPlanCreateCommandDto;
import com.miruni.backend.domain.plan.dto.request.AiRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.global.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.concurrent.TimeoutException;
import org.springframework.cache.annotation.Cacheable;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
    private final GeminiConfig config;
    private final GeminiParser parser;

    private boolean isRetryableException(Throwable ex) {
        if (ex instanceof WebClientRequestException) {
            return true;
        }
        if (ex instanceof WebClientResponseException responseException) {
            return responseException.getStatusCode().is5xxServerError();
        }
        if (ex instanceof TimeoutException || ex instanceof io.netty.handler.timeout.ReadTimeoutException) {
            return true;
        }
        return false;
    }

    @Cacheable(value = "aiPlans", key = "#command.title + #command.deadline + #command.taskRange", cacheManager = "cacheManager")
    public Mono<List<AiPlanCreateResponse>> getAiPlanFromApi(AiPlanCreateCommandDto command) {
        String prompt = buildPrompt(command);
        AiRequest aiRequest = AiRequest.fromPrompt(prompt);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", config.getApiKey()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(GeminiParser.AiResponse.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(1))
                                .filter(this::isRetryableException)
                                .doBeforeRetry(signal -> log.info("네트워크 불안정 감지 - 재시도 중..({}회)", signal.totalRetries() + 1))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()
                                ))
                )
                .map(aiResponse -> parser.parseToDto(aiResponse, command))
                .name("gemini.api.request")
                .metrics();
    }

    private String buildPrompt(AiPlanCreateCommandDto command) {
        return String.format(
                """
                        넌 이제부터 일정 관리자야. JSON 배열만 출력해줘. 설명·코드블록·마크업 금지.
                        아래 내용을 보고 최소 2개, 최대 10개 단계로 세부 일정들로 나눠줘.
                        
                        JSON 배열은 다음과 같은 키들로만 포함하는 객체들로 구성되어야 해:
                        - "scheduledDate": (string, "YYYY-MM-DD")
                        - "subTitle": (string, sub-task title)
                        - "expectedDuration": (number, in minutes)
                        - "startTime": (string, "HH:MM:SS")
                        - "endTime": (string, "HH:MM:SS")
                        
                        작업 시간대는 7개의 선택지가 있고, 이에 해당하는 시간대에 맞춰 일정을 세워줘.
                        
                        업무 정보:
                        - 제목: %s
                        - 마감기한: %s
                        - 작업 시간대: %s
                        - 작업 범위: %s
                        - 우선 순위: %s
                        - 세부 요청사항: %s
                        
                        """,
                command.title(), command.deadline(), command.timePeriod(),
                command.taskRange(), command.priority(), command.detailRequest()
        );
    }

}
