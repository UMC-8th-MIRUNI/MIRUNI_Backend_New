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

    @Cacheable(value = "aiPlans", key = "#command.title + #command.endDateTime + #command.scope", cacheManager = "cacheManager")
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
                You are an expert Schedule Manager.
                Output ONLY a raw JSON array. Do not include markdown formatting (e.g., ```json), explanations, or any other text.
                
                Based on the information below, break down the work into detailed sub-tasks.
                - The number of sub-tasks must be between 2 and 10.
                - Schedule the dates and times logically between StartDateTime and EndDateTime.
                
                The JSON array must consist of objects containing strictly the following keys:
                - "scheduledDate": (string, "YYYY-MM-DD")
                - "subTitle": (string, sub-task title)
                - "expectedDuration": (number, in minutes)
                - "startTime": (string, "HH:MM:SS")
                - "endTime": (string, "HH:MM:SS")
                
                [Time Slot Definitions]
                - RANDOM : Random time
                - MORNING : 06:00 ~ 09:00
                - FOCUS_MORNING : 09:00 ~ 12:00
                - AFTERNOON : 13:00 ~ 17:00
                - EVENING : 18:00 ~ 21:00
                - NIGHT : 22:00 ~ 23:59
                - DAWN : 00:00 ~ 06:00
                
                [Scheduling Rules]
                1. Try to fit tasks into the 'Preferred Time Slot' if possible.
                2. If StartDateTime and EndDateTime are on the same day (Single-day Task):
                   - You MUST schedule all tasks within that single day.
                   - If the tasks cannot fit into the 'Preferred Time Slot', you are allowed to extend beyond the preferred slot to ensure all tasks are completed within the day.
                3. If StartDateTime and EndDateTime are different (Multi-day Task):
                   - Distribute tasks logically across the days within the 'Preferred Time Slot'.
                4. Chronological Order: The final JSON array MUST be sorted strictly by 'scheduledDate' and 'startTime'. The earliest task must appear first in the array.
                
                [Task Information]
                - Title: %s
                - StartDateTime: %s
                - EndDateTime: %s
                - Preferred Time Slot: %s
                - Scope: %s
                - Priority: %s
                - Details: %s
                """,
                command.title(), command.startDateTime(), command.endDateTime(), command.timePeriod(),
                command.scope(), command.priority(), command.detailRequest()
        );
    }

}
