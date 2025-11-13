package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name="plan", description = "일정 분할/조회/관리 API")
public interface AiPlanApi {

    @Operation(
            summary = "AI 기반 일정 분할 API",
            description = "사용자가 상위 일정 정보를 AiPlanCreateRequest로 전달하면, AI가 이를 세부 일정으로 분할하여 저장하고 전체 목록을 반환합니다."
            )
//    @SecurityRequirement(name = "JWT") //
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 일정 생성 및 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "AI 일정 생성 성공",
                                    value = """
                                    {
                                        "errorCode": null,
                                        "message": "OK",
                                        "result": [
                                            {
                                                "planId": 1,
                                                "ai_planId": 1,
                                                "title": "UMC 기획안 만들기",
                                                "deadline": "2026-01-01",
                                                "taskRange": "앱 기획, 디자인, 프론트, 백엔드, 배포",
                                                "priority": "HIGH",
                                                "scheduled_date": "2025-11-11",
                                                "description": "프로젝트 기획 및 MVP 정의",
                                                "expected_duration": 120,
                                                "startTime": "09:00:00",
                                                "endTime": "11:00:00"
                                            },
                                            {
                                                "planId": 1,
                                                "ai_planId": 2,
                                                "title": "UMC 기획안 만들기",
                                                "deadline": "2026-01-01",
                                                "taskRange": "앱 기획, 디자인, 프론트, 백엔드, 배포",
                                                "priority": "HIGH",
                                                "scheduled_date": "2025-11-12",
                                                "description": "와이어프레임 및 기본 UI 디자인",
                                                "expected_duration": 120,
                                                "startTime": "09:00:00",
                                                "endTime": "11:00:00"
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Request Body 유효성 검증 실패",
                                    value = """
                                    {
                                        "status": 400,
                                        "errorCode": "COMMON_002",
                                        "message": "입력값 검증에 실패했습니다."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "AI 응답 처리 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "AI 응답 없음",
                                            value = """
                                            {
                                                "status": 404,
                                                "errorCode": "AI_PLAN_001",
                                                "message": "AI로부터 유효한 응답을 받지 못했습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "AI 응답 파싱 실패",
                                            value = """
                                            {
                                                "status": 404,
                                                "errorCode": "AI_PLAN_002",
                                                "message": "AI 응답을 파싱하는 데 실패했습니다."
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    Mono<ResponseEntity<List<AiPlanCreateResponse>>> createAiPlan(
            @RequestParam Long userId,
            @RequestBody @Valid AiPlanCreateRequest request
            );
}
