package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.SurveyRequest;
import com.miruni.backend.domain.user.dto.response.SurveyResponse;
import com.miruni.backend.domain.user.dto.response.UserSurveyResponse;
import com.miruni.backend.global.authroize.LoginUser;
import com.miruni.backend.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "설문조사 API", description = "사용자 설문조사 조회/수정 API")
public interface SurveyApi {

    @Operation(
            summary = "사용자 설문조사 결과 조회",
            description = "현재 로그인한 사용자의 설문조사 결과를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                          "errorCode": null,
                          "message": "OK",
                          "result": {
                            "situationDescriptions": ["PHONE"],
                            "delayRange": 3,
                            "reasonDescriptions": ["PERFECTIONISM"]
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    UserSurveyResponse getUserSurveyResult(@LoginUser Long userId);

    @Operation(
            summary = "사용자 설문조사 수정/저장",
            description = "현재 로그인한 사용자의 설문조사 내용을 저장하거나 수정합니다."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정/저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                          "errorCode": null,
                          "message": "OK",
                          "result": {
                            "message": "설문조사가 수정되었습니다!",
                            "completedAt": "2024-01-01T12:00:00",
                            "status": "UPDATED"
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    SurveyResponse updateSurvey(
            @LoginUser Long userId,
            @Valid @RequestBody SurveyRequest request
    );
}

