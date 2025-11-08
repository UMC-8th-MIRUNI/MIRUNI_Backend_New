package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.BasicPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Basic Plan API", description = "일반 일정 관련 API")
public interface BasicPlanApi {

    @Operation(summary = "일반 일정 생성", description = "사용자가 새로운 일반 일정을 생성합니다.")
    BasicPlanResponse createBasicPlan(
            @RequestParam Long userId,
            @Valid @RequestBody BasicPlanCreateRequest request
    );
}