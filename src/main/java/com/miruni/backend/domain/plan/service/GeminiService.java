package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.global.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
    private final GeminiConfig config;
    private final GeminiParser parser;


    public Mono<List<AiPlanCreateResponse>> getAiPlanFromApi(AiPlanCreateRequest request,  Long planId) {
        String prompt = buildPrompt(request);
        AiRequest aiRequest = AiRequest.fromPrompt(prompt);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", config.getApiKey()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(GeminiParser.AiResponse.class)
                .map(aiResponse -> parser.parseToDto(aiResponse, request, planId));
    }

    private String buildPrompt(AiPlanCreateRequest request) {
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
                        
                        업무 정보:
                        - 제목: %s
                        - 마감기한: %s
                        - 작업 시간대: %s
                        - 작업 범위: %s
                        - 우선 순위: %s
                        - 세부 요청사항: %s
                        
                        """,
                request.title(), request.deadline(), request.timePeriod(),
                request.taskRange(), request.priority(), request.detailRequest()
        );
    }

}
