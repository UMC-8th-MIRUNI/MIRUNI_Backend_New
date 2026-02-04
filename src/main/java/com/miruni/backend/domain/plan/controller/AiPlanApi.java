package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlansDeleteRequest;
import com.miruni.backend.domain.plan.dto.request.PlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.*;
import com.miruni.backend.global.authroize.CustomUserDetails;
import com.miruni.backend.global.authroize.LoginUser;
import com.miruni.backend.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name="AI Plan API", description = "AI 일정 관련 API")
public interface AiPlanApi {

    @Operation(
            summary = "AI 플래닝 API",
            description = "사용자가 상위 일정 정보를 AiPlanCreateRequest로 전달하면, AI가 이를 세부 일정으로 분할하여 저장하고 전체 목록을 반환합니다."
            )
    @SecurityRequirement(name = "JWT")
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
    List<AiPlanCreateResponse> createAiPlan(
            @LoginUser Long userId,
            @RequestBody @Valid AiPlanCreateRequest request
    );

    @Operation(
            summary = "AI 일정 수정 API ",
            description = "특정 ai_plan_id를 기준으로, 하위 일정(AiPlan) 및 상위 일정(Plan)의 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AiPlanUpdateResponse.class),
                            examples = @ExampleObject(
                                    name = "수정 성공",
                                    value = """
                                    {
                                        "title": "상위일정 수정됨",
                                        "description": "하위일정 수정됨",
                                        "scheduled_date": "2027-01-01",
                                        "startTime": "14:20:59",
                                        "updated_at": "2025-11-14T10:05:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 (사용자 ID 불일치)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 일정",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    AiPlanUpdateResponse updateAiPlan(
            @LoginUser Long userId,
            @PathVariable("ai_plan_id") Long ai_plan_id,
            @RequestBody @Valid AiPlanUpdateRequest request
    );

    @Operation(
            summary = "AI 일정 삭제 API",
            description = "특정 ai_plan_id의 세부 일정을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AiPlanDeleteResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공",
                                    value = "{\"isDeleted\": true}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "일정/사용자 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    AiPlanDeleteResponse deleteAiPlan(
            @PathVariable("ai_plan_id") Long ai_plan_id,
            @LoginUser Long user_id
    );

    @Operation(
            summary = "AI 상위 일정 조회 API",
            description = "사용자의 잔여 AI 횟수와 현재 진행 중인 일정 리스트(진행률 포함)를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlanReadResponse.class),
                            examples = @ExampleObject(
                                    name = "메인 화면 조회 예시",
                                    value = """
                                        {
                                          "remainingAiCnt": 3,
                                          "plans": [
                                            {
                                              "planId": 1,
                                              "title": "UMC 기획안 만들기",
                                              "doneCnt": 3,
                                              "totalCnt": 10,
                                              "progressPercentage": 30,
                                              "isDone": false
                                            },
                                            {
                                              "planId": 2,
                                              "title": "스프링 공부하기",
                                              "doneCnt": 5,
                                              "totalCnt": 5,
                                              "progressPercentage": 100,
                                              "isDone": true
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    PlanReadResponse readPlan(
            @LoginUser Long userId
    );

    @Operation(
            summary = "AI 플래닝 스케줄표 조회 API",
            description = "해당 상위 일정에 대한 정보와 이에 속해 있는 세부 일정 리스트를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AiPlanResponse.class),
                            examples = @ExampleObject(
                                    name = "세부 일정 조회 예시",
                                    value = """
                                        {
                                          "planId": 1,
                                          "title" : "UMC 기획안 만들기",
                                          "deadline" : "2025-05-30",
                                          "taskRange" : "기획안 13페이지 작성",
                                          "priority" : "HIGH",
                                          "progressPercentage" : 30,
                                          "aiPlans": [
                                            {
                                              "aiPlanId": 1,
                                              "subTitle": "기존 서비스 레퍼런스 조사",
                                              "scheduledDate": "2025-01-10",
                                              "startTime": "14:00:00",
                                              "endTime: "15:00:00",
                                              "expectedDuration": 60
                                            },
                                            {
                                              "aiPlanId": 2,
                                              "subTitle": "기능 명세서 초안 작성",
                                              "scheduledDate": "2025-01-11",
                                              "startTime": "10:00:00",
                                              "endTime" : "12:00:00",
                                              "expectedDuration": 120
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (본인 일정이 아님)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "해당 일정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    AiPlanResponse readAiPlan(
            @Parameter(description = "조회할 상위 일정의 ID", example = "1")
            @PathVariable("plan-id") Long planId,
            @LoginUser Long userId
    );

    @Operation(
            summary = "AI 플래닝 스케줄표 삭제 API",
            description = "특정 상위 일정과 그에 속하는 세부일정 모두를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlanDeleteAllResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공 예시",
                                    value = """
                                        {
                                          "isDeleted": true
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음 (본인의 일정이 아님)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "해당 일정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    PlanDeleteAllResponse deletePlanTable(
            @Parameter(description = "삭제할 상위 일정의 ID", example = "1")
            @PathVariable("plan-id") Long planId,

            @LoginUser Long userId
    );

    @Operation(
            summary = "AI 플래닝 스케줄표 수정 API",
            description = """
            상위 일정의 정보(제목, 마감기한 등)와 세부 일정(스케줄표)을 수정합니다.
            
            **주의사항:**
            1. 수정하지 않을 상위 일정 필드는 생략(null) 가능.
            2. `aiPlans` 내부의 `aiPlanId`는 필수값.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AiPlanResponse.class),
                            examples = @ExampleObject(
                                    name = "수정 성공 예시",
                                    value = """
                                        {
                                          "planId": 1,
                                          "title": "UMC 기획안 최종 수정",
                                          "deadline": "2025-06-01",
                                          "taskRange": "전체 검수 및 제출",
                                          "priority": "HIGH",
                                          "aiPlans": [
                                            {
                                              "aiPlanId": 1,
                                              "scheduledDate": "2025-05-31",
                                              "startTime": "10:00:00",
                                              "endTime": "12:00:00",
                                              "subTitle": "오타 검수하기",
                                              "expectedDuration": 120
                                            },
                                            {
                                              "aiPlanId": 2,
                                              "scheduledDate": "2025-06-01",
                                              "startTime": "14:00:00",
                                              "endTime": "15:00:00",
                                              "subTitle": "최종 제출",
                                              "expectedDuration": 60
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 일정이 아님)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "일정 또는 세부 일정을 찾을 수 없음 (잘못된 ID)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    AiPlanResponse updatePlanTable(
            @Parameter(description = "수정할 상위 일정의 ID", example = "1")
            @PathVariable("plan-id") Long planId,
            @LoginUser Long userId,
            @RequestBody @Valid PlanUpdateRequest request
    );

    @Operation(
            summary = "AI 세부 일정 선택 삭제 API",
            description = """
        특정 상위 일정에 속한 세부 일정(스케줄표 행)들을 선택하여 일괄 삭제합니다.
        
        **주의사항:**
        1. 삭제할 세부 일정들의 ID(`aiPlansId`)를 리스트에 담아 요청해야 합니다.
        2. 요청한 세부 일정들이 해당 상위 일정(`plan-id`)에 속해있지 않다면 에러가 발생합니다.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AiPlansDeleteResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공 예시",
                                    value = """
                                        {
                                          "isDeleted": true
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (상위 일정이 일치하지 않음)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 일정이 아님)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "일정 또는 세부 일정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    AiPlansDeleteResponse deleteAiPlanItems(
            @Parameter(description = "상위 일정의 ID", example = "1")
            @PathVariable("plan-id") Long planId,
            @LoginUser Long userId,
            @RequestBody @Valid AiPlansDeleteRequest request
    );


}
