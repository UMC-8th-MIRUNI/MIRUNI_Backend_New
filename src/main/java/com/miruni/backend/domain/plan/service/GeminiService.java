package com.miruni.backend.domain.plan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiResponse;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.global.exception.BaseException;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void init(){
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }


    public Mono<List<AiPlanCreateResponse>> getAiPlanFromApi(AiPlanCreateRequest request,  Long planId) {
        String prompt = buildPrompt(request);
        AiRequest aiRequest = AiRequest.fromPrompt(prompt);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(AiResponse.class)
                .flatMap(aiResponse -> {
                    String text = aiResponse.getFirstCandidateText()
                            .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_RESPONSE_EMPTY));

                    return parseAndMapResponse(text, request, planId);
                });
    }

    private String buildPrompt(AiPlanCreateRequest request) {
        return String.format(
                """
                        넌 이제부터 일정 관리자야. JSON 배열만 출력해줘. 설명·코드블록·마크업 금지.
                        아래 내용을 보고 최소 2개, 최대 10개 단계로 세부 일정들로 나눠줘.
                        
                        JSON 배열은 다음과 같은 키들로만 포함하는 객체들로 구성되어야 해:
                        - "scheduled_date": (string, "YYYY-MM-DD")
                        - "description": (string, sub-task title)
                        - "expected_duration": (number, in minutes)
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

    private Mono<List<AiPlanCreateResponse>> parseAndMapResponse(String jsonText, AiPlanCreateRequest request, Long planId) {
        try{
            TypeReference<List<AiPlanStepDto>> typeRef = new TypeReference<>() {};
            List<AiPlanStepDto> aiSteps = objectMapper.readValue(jsonText, typeRef);

            List<AiPlanCreateResponse> responseList = aiSteps.stream()
                    .map(step -> new AiPlanCreateResponse(
                            planId, 1L,
                            request.title(), request.deadline(),request.taskRange(),request.priority(),
                            step.scheduled_date(), step.description(),
                            step.expected_duration(), step.startTime(), step.endTime()
                    ))
                    .toList();
            return Mono.just(responseList);
        }catch (JsonProcessingException e){
            return Mono.error(BaseException.type(AiPlanErrorCode.AI_RESPONSE_PARSING_FAILED));
        }
    }

    private record AiPlanStepDto(
            LocalDate scheduled_date,
            String description,
            Long expected_duration,
            LocalTime startTime,
            LocalTime endTime
    ) {}

}
