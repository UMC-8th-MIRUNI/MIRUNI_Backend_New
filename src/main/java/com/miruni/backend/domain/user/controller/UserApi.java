package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.EmailVerificationRequest;
import com.miruni.backend.domain.user.dto.request.EmailVerificationVerifyRequest;
import com.miruni.backend.domain.user.dto.request.ResetPasswordRequest;
import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.VerifyResponse;
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
            description = "이름, 생년월일, 전화번호, 이메일, 비밀번호, 닉네임으로 회원가입합니다. \n" +
                    "이메일, 닉네임, 전화번호 중복 체크 후 비밀번호를 암호화하여 저장하고, \n" +
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
            @ApiResponse(responseCode = "409", description = "중복된 이메일, 닉네임 또는 전화번호",
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
                                "status": 409,
                                "errorCode": "USER404_1",
                                "message": "이미 사용 중인 닉네임입니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "전화번호 중복",
                                            summary = "이미 사용 중인 전화번호",
                                            value = """
                            {
                                "status": 409,
                                "errorCode": "USER409_3",
                                "message": "이미 사용 중인 전화번호입니다."
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
    JwtResponseDto signup(@Valid @RequestBody UserSignupRequest request);

    @Operation(
            summary = "회원가입 이메일 인증코드 요청",
            description = "회원가입 시 입력한 이메일로 6자리 인증코드를 발송합니다. \n" +
                    "인증코드는 5분 동안만 유효하며, 추후 별도의 인증 코드 검증 API에서 사용됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증코드 발송 성공",
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
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 형식 오류",
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
    void requestEmailVerification(@Valid @RequestBody EmailVerificationRequest request);

    @Operation(
            summary = "회원가입 이메일 인증코드 검증",
            description = "이메일과 6자리 인증코드를 검증합니다. \n" +
                    "코드가 일치하고 유효기간(5분) 이내라면 인증에 성공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공",
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
            @ApiResponse(responseCode = "400", description = "코드 만료 또는 불일치",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "코드 없음 또는 만료",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_9",
                            "message": "이메일 인증 코드가 존재하지 않거나 만료되었습니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "코드 불일치",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_10",
                            "message": "이메일 인증 코드가 올바르지 않습니다."
                        }
                        """
                                    )
                            }
                    )
            )
    })
    void verifyEmailVerification(@Valid @RequestBody EmailVerificationVerifyRequest request);

    @Operation(
            summary = "비밀번호 재설정 이메일 요청",
            description = "입력한 이메일로 비밀번호 재설정용 인증코드를 발송합니다. \n" +
                    "요청한 이메일이 실제 계정과 매칭되지 않더라도, 보안상의 이유로 항상 동일한 응답을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 접수 완료",
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
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 형식 오류",
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
    void requestPasswordReset(@Valid @RequestBody EmailVerificationRequest request);

    @Operation(
            summary = "비밀번호 재설정 코드 검증",
            description = "비밀번호 재설정 시 발송된 6자리 인증코드를 검증합니다. \n" +
                    "코드가 일치하고 유효기간(5분) 이내라면 인증에 성공하며, \n" +
                    "비밀번호 변경에 사용할 임시 resetToken을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.miruni.backend.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": {
                                "resetToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
                            }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "코드 만료 또는 불일치",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "코드 없음 또는 만료",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_9",
                            "message": "이메일 인증 코드가 존재하지 않거나 만료되었습니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "코드 불일치",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_10",
                            "message": "이메일 인증 코드가 올바르지 않습니다."
                        }
                        """
                                    )
                            }
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
    VerifyResponse verifyPasswordResetCode(@Valid @RequestBody EmailVerificationVerifyRequest request);

    @Operation(
            summary = "비밀번호 재설정 완료",
            description = "비밀번호 재설정 코드 검증으로 발급받은 resetToken을 사용해 새 비밀번호로 재설정합니다. \\n" +
                    "토큰이 만료되었거나 유효하지 않으면 실패합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
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
            @ApiResponse(responseCode = "400", description = "비밀번호 재설정 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 또는 만료된 토큰",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER401_7",
                            "message": "유효하지 않은 토큰입니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "새 비밀번호가 기존 비밀번호와 동일",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_15",
                            "message": "새 비밀번호는 현재 비밀번호와 달라야 합니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "소셜 로그인 사용자",
                                            value = """
                        {
                            "status": 400,
                            "errorCode": "USER400_13",
                            "message": "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."
                        }
                        """
                                    )
                            }
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
                            "errorCode": "USER404_4",
                            "message": "사용자를 찾을 수 없습니다."
                        }
                        """
                            )
                    )
            )
    })
    void resetPassword(@Valid @RequestBody ResetPasswordRequest request);

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
