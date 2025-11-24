package com.miruni.backend.domain.plan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlanDurationResponse {

    private String planType;
    private Long id;
    private Long expectedDuration;

    public static PlanDurationResponse of(String planType, Long id, Long expectedDuration) {
        return PlanDurationResponse.builder()
                .planType(planType)
                .id(id)
                .expectedDuration(expectedDuration)
                .build();
    }
}