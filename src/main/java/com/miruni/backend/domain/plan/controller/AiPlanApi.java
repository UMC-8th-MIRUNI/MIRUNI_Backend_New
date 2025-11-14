package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanDeleteResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanUpdateResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(
            summary = "세부 분할 일정 수정 API ",
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
            @RequestParam Long userId,
            @PathVariable("ai_plan_id") Long ai_plan_id,
            @RequestBody @Valid AiPlanUpdateRequest request
    );

    @Operation(
            summary = "세부 일정 삭제 API",
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
            @RequestParam("user_id") Long user_id
    );

}
