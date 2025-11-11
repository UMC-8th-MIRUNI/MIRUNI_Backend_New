package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.response.UserResponse;
import com.miruni.backend.global.authroize.AuthToken;
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

@Tag(name = "인증 API", description = "로그인, 로그아웃 등 인증 관련 API")
public interface AuthApi {

    @Operation(
            summary = "일반 로그인",
            description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": {
                                "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
                                "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
                                "tokenType": "Bearer",
                                "accessTokenExpiresIn": 3600,
                                "refreshTokenExpiresIn": 604800
                            }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "비밀번호가 올바르지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "비밀번호 불일치",
                                    value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_4",
                            "message": "비밀번호가 올바르지 않습니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                        {
                            "status": 404,
                            "errorCode": "USER404_3",
                            "message": "사용자를 찾을 수 없습니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "검증 실패",
                                    value = """
                        {
                            "status": 400,
                            "errorCode": "COMMON_002",
                            "message": "입력값 검증에 실패했습니다."
                        }
                        """
                            )
                    )
            )
    })
    UserResponse login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "로그아웃",
            description = "현재 사용자의 액세스 토큰을 블랙리스트에 추가하고 리프레시 토큰을 삭제합니다."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            summary = "JWT 토큰 인증 실패",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "COMMON_003",
                                "message": "인증이 필요합니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            summary = "액세스 토큰이 유효하지 않음",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "USER401_6",
                                "message": "유효하지 않은 토큰입니다."
                            }
                            """
                                    )
                            }
                    )
            )
    })
    void logout(
            @AuthToken String accessToken,
            @LoginUser Long userId
    );
}