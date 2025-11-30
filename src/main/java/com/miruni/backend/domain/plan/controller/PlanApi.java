package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.PlanFinishRequest;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.type.PlanType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Plan Execution", description = "일정 실행 관련 API")
public interface PlanApi {

    @Operation(summary = "일정 시작(플랜 예상 소요 시간 조회)",
            description = "planType(BASIC 또는 AI)과 id를 기반으로 예상 소요 시간을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플랜 없음")
    })
    PlanDurationResponse getExpectedDuration(@RequestParam Long userId,
                                             @RequestParam PlanType planType,
                                             @RequestParam Long id);

    @Operation(summary = "일정 완료",
            description = "사용자가 수행한 시간을 기반으로 땅콩 계산 및 일정 완료 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "플랜 또는 사용자 없음")
    })
    PlanFinishResponse finishPlan(
            @RequestParam Long userId,
            @Valid @RequestBody PlanFinishRequest request
    );
}
