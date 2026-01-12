package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;
import com.miruni.backend.domain.plan.dto.request.BasicPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.global.authroize.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Basic Plan API", description = "일반 일정 관련 API")
public interface BasicPlanApi {

    @Operation(summary = "일반 일정 생성", description = "사용자가 새로운 일반 일정을 생성합니다.")
    List<BasicPlanResponse> createBasicPlan(@LoginUser Long userId,
                                            @Valid @RequestBody BasicPlanSaveRequest request);

    @Operation(summary = "일반 일정 수정", description = "일반 일정을 수정합니다.")
    BasicPlanResponse updateBasicPlan(@LoginUser Long userId,
                                      @PathVariable Long basicPlanId,
                                      @Valid @RequestBody BasicPlanUpdateRequest request);

    @Operation(summary = "일반 일정 삭제", description = "일반 일정을 삭제합니다.")
    Long deleteBasicPlan(@LoginUser Long userId,
                         @PathVariable Long basicPlanId);
}