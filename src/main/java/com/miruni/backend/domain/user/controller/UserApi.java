package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
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

@Tag(name = "사용자 API", description = "회원가입 등 사용자 관련 API")
public interface UserApi {

    @Operation(
            summary = "일반 회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원가입합니다. \n" +
                    "이메일 중복 체크 후 비밀번호를 암호화하여 저장하고, \n" +
                    "회원가입 성공 시 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
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
            @ApiResponse(responseCode = "409", description = "중복된 이메일 또는 닉네임",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이메일 중복",
                                            summary = "이미 사용 중인 이메일",
                                            value = """
                            {
                                "status": 409,
                                "errorCode": "USER409_2",
                                "message": "이미 사용 중인 이메일입니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "닉네임 중복",
                                            summary = "이미 사용 중인 닉네임",
                                            value = """
                            {
                                "status": 404,
                                "errorCode": "USER404_1",
                                "message": "이미 사용 중인 닉네임입니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 필수 약관 미동의",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "이메일, 비밀번호 형식 오류 등",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "필수 약관 미동의",
                                            summary = "서비스 이용약관 동의 필수",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_5",
                                "message": "필수 약관에 동의해야 합니다."
                            }
                            """
                                    )
                            }
                    )
            )
    })
    UserResponse signup(@Valid @RequestBody UserSignupRequest request);

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. \n" +
                    "소프트 삭제 방식으로 처리되며, 모든 토큰이 무효화됩니다."
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
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
            @ApiResponse(responseCode = "400", description = "이미 탈퇴한 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이미 탈퇴한 사용자",
                                    value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_7",
                            "message": "이미 탈퇴한 사용자입니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
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
    void withdrawUser(
            @AuthToken String accessToken,
            @LoginUser Long userId
    );
}
