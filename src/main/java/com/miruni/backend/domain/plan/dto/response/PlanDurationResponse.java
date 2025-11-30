package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.type.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlanDurationResponse {

    private PlanType planType;
    private Long id;
    private Long expectedDuration;

    public static PlanDurationResponse of(PlanType planType, Long id, Long expectedDuration) {
        return PlanDurationResponse.builder()
                .planType(planType)
                .id(id)
                .expectedDuration(expectedDuration)
                .build();
    }
}
