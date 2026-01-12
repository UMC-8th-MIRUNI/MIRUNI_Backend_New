package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.GoogleLoginRequest;
import com.miruni.backend.domain.user.dto.request.KakaoLoginRequest;
import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.request.ReissueTokenRequest;
import com.miruni.backend.domain.user.dto.request.SocialSignupCompleteRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.SocialLoginResponseDto;
import com.miruni.backend.domain.user.entity.OauthProvider;
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
import org.springframework.web.bind.annotation.PathVariable;
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
    JwtResponseDto login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "액세스/리프레시 토큰 재발급",
            description = "유효한 리프레시 토큰을 가진 인증된 사용자에게 새로운 액세스/리프레시 토큰 세트를 발급합니다. (Refresh Token Rotation)"
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
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
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "유효하지 않은 리프레시 토큰",
                                    value = """
                        {
                          "status": 401,
                          "errorCode": "USER401_6",
                          "message": "유효하지 않은 토큰입니다."
                        }
                        """
                            )
                    )
            )
    })
    JwtResponseDto refreshToken(
            @LoginUser Long userId,
            @Valid @RequestBody ReissueTokenRequest request
    );

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

    @Operation(
            summary = "구글 소셜 로그인",
            description = "구글 ID 토큰으로 소셜 로그인을 수행하고, 신규/기존 여부 및 회원가입 필요 여부를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                          "errorCode": null,
                          "message": "OK",
                          "result": {
                            "signupRequired": true,
                            "newUser": true,
                            "tokens": {
                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "tokenType": "Bearer",
                              "accessTokenExpiresIn": 3600,
                              "refreshTokenExpiresIn": 604800
                            }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 구글 토큰",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "소셜 토큰 검증 실패",
                                    value = """
                        {
                          "status": 401,
                          "errorCode": "USER401_8",
                          "message": "유효하지 않은 소셜 로그인 토큰입니다."
                        }
                        """
                            )
                    )
            )
    })
    SocialLoginResponseDto loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request);

    @Operation(
            summary = "카카오 소셜 로그인",
            description = "카카오 액세스 토큰으로 소셜 로그인을 수행하고, 신규/기존 여부 및 회원가입 필요 여부를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                          "errorCode": null,
                          "message": "OK",
                          "result": {
                            "signupRequired": false,
                            "newUser": false,
                            "tokens": {
                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "tokenType": "Bearer",
                              "accessTokenExpiresIn": 3600,
                              "refreshTokenExpiresIn": 604800
                            }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 카카오 토큰",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "소셜 토큰 검증 실패",
                                    value = """
                        {
                          "status": 401,
                          "errorCode": "USER401_8",
                          "message": "유효하지 않은 소셜 로그인 토큰입니다."
                        }
                        """
                            )
                    )
            )
    })
    SocialLoginResponseDto loginWithKakao(@Valid @RequestBody KakaoLoginRequest request);

    @Operation(
            summary = "소셜 회원가입 완료",
            description = "가입 미완료(ROLE_GUEST, PENDING_SIGNUP) 소셜 유저가 필수 약관 및 닉네임을 제출하여 최종 회원가입을 완료합니다."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료 및 JWT 발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                          "errorCode": null,
                          "message": "OK",
                          "result": {
                            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "accessTokenExpiresIn": 3600,
                            "refreshTokenExpiresIn": 604800
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 약관/닉네임 관련 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "약관 미동의",
                                            value = """
                        {
                          "status": 400,
                          "errorCode": "USER400_5",
                          "message": "필수 약관에 동의해야 합니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "닉네임 중복",
                                            value = """
                        {
                          "status": 400,
                          "errorCode": "USER404_1",
                          "message": "이미 사용 중인 닉네임입니다."
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                        {
                          "status": 401,
                          "errorCode": "COMMON_003",
                          "message": "인증이 필요합니다."
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
            )
    })
    JwtResponseDto completeSocialSignup(
            @PathVariable("provider") OauthProvider provider,
            @LoginUser Long userId,
            @Valid @RequestBody SocialSignupCompleteRequest request
    );
}