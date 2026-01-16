package com.miruni.backend.domain.plan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miruni.backend.domain.plan.dto.command.AiPlanCreateCommandDto;
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
    private final PlanQueryService planQueryService;

    public GeminiParser(PlanQueryService planQueryService) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.planQueryService = planQueryService;
    }

    public List<AiPlanCreateResponse> parseToDto(AiResponse aiResponse, AiPlanCreateCommandDto command) {
        String jsonText = extractText(aiResponse);

        return mapJsonToDto(jsonText, command);
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

    private List<AiPlanCreateResponse> mapJsonToDto(String jsonText, AiPlanCreateCommandDto command) {
        try{
            List<AiPlanStepDto> aiSteps = objectMapper.readValue(jsonText, new TypeReference<>() {}) ;

            return aiSteps.stream()
                    .map(step -> new AiPlanCreateResponse(
                            command.planId(), 1L,
                            command.title(), command.startDateTime().toLocalDate(), command.scope(), command.priority(),
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
            int expectedDuration,
            LocalTime startTime,
            LocalTime endTime
    ) {}
}
