package com.miruni.backend.domain.user.controller;

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

@Tag(name = "사용자 API", description = "사용자 정보 및 계정 관리 API")
public interface UserApi {

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. \\n" +
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
                            "errorCode": "USER400_8",
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
                            "errorCode": "USER404_4",
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
