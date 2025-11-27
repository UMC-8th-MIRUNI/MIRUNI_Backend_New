package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.response.DailyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.PlanDetailResponse;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.authroize.LoginUser;
import com.miruni.backend.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name="plan", description = "일정 분할/조회/관리 API")
public interface PlanApi {

    @Operation(
            summary = "캘린더 조회",
            description = "특정 년/월의 날짜별 미완료 일정 개수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "캘린더 조회 성공")
    })
    List<MonthlyPlanResponse> getMonthlyPlans(@LoginUser Long userId, @RequestParam int year, @RequestParam int month);

    @Operation(
            summary = "특정 날짜의 완료/미완료 일정 조회",
            description = "특정 날짜의 완료/미완료 일정을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 조회 성공")
    })
    DailyPlanResponse getDailyPlans(@LoginUser Long userId, @RequestParam int year, @RequestParam int month, @RequestParam int day);

    @Operation(
            summary = "특정 일정 조회",
            description = "특정 일정을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 일정에 권한 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 일정",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    PlanDetailResponse getPlanDetail(@LoginUser Long userId, @PathVariable Long planId, @RequestParam PlanType planType);
}
