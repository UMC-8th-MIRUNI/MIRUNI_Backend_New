package com.miruni.backend.domain.plan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.global.exception.BaseException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class GeminiParser {
    private final ObjectMapper objectMapper;

    public GeminiParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<AiPlanCreateResponse> parseToDto(AiResponse aiResponse, AiPlanCreateRequest request, Long planId) {
        String jsonText = extractText(aiResponse);

        return mapJsonToDto(jsonText, request, planId);
    }

    private String extractText(AiResponse aiResponse) {
        if (aiResponse.candidates() != null && !aiResponse.candidates().isEmpty()) {
            AiResponse.Candidate firstCandidate = aiResponse.candidates().get(0);
            if (firstCandidate.content() != null &&
                    firstCandidate.content().parts() != null &&
                    !firstCandidate.content().parts().isEmpty()) {

                String rawText = firstCandidate.content().parts().get(0).text();
                return rawText.replace("```json", "").replace("```", "").trim();
            }
        }
        throw BaseException.type(AiPlanErrorCode.AI_RESPONSE_EMPTY);
    }

    private List<AiPlanCreateResponse> mapJsonToDto(String jsonText, AiPlanCreateRequest request, Long planId) {
        try{
            List<AiPlanStepDto> aiSteps = objectMapper.readValue(jsonText, new TypeReference<>() {}) ;

            return aiSteps.stream()
                    .map(step -> new AiPlanCreateResponse(
                            planId, 1L,
                            request.title(), request.deadline(), request.taskRange(), request.priority(),
                            step.scheduledDate(), step.subTitle(), step.expectedDuration(),
                            step.startTime(), step.endTime()
                    ))
                    .toList();
        }catch (JsonProcessingException e){
            throw BaseException.type(AiPlanErrorCode.AI_RESPONSE_PARSING_FAILED);
        }
    }

    public record AiResponse(List<Candidate> candidates){
        public record Candidate(Content content) {}
        public record Content(List<Part> parts){}
        public record Part(String text){}
    }

    private record AiPlanStepDto(
            LocalDate scheduledDate,
            String subTitle,
            Long expectedDuration,
            LocalTime startTime,
            LocalTime endTime
    ) {}
}
