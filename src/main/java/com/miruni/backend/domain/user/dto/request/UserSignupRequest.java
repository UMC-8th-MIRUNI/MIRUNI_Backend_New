package com.miruni.backend.domain.user.dto.request;

import com.miruni.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record UserSignupRequest(

        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
        @Schema(description = "사용자 이름", example = "김철수")
        String name,

        @NotBlank(message = "생년월일은 필수입니다.")
        @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리 숫자여야 합니다. (예: 19990101)")
        @Schema(description = "생년월일 (YYYYMMDD)", example = "19990101")
        String birthDate,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.")
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
        @Schema(description = "이메일 주소", example = "dhzktldh@gmail.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        @Schema(description = "사용자 닉네임", example = "추추")
        String nickname,

        @NotNull(message = "이용약관 동의는 필수입니다.")
        @Schema(description = "이용약관 동의", example = "true")
        Boolean serviceAgreed,

        @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
        @Schema(description = "개인정보 수집 및 이용 동의", example = "true")
        Boolean privacyAgreed,

        @Schema(description = "마케팅 정보 수신 동의 (선택)", example = "false")
        Boolean marketingAgreed

) {
        public User toEntity(String encodedPassword) {
                // 생년월일 문자열을 LocalDate로 변환 (YYYYMMDD -> LocalDate)
                LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                
                // 전화번호에서 하이픈 제거
                String normalizedPhoneNumber = phoneNumber.replace("-", "");
                
                return User.builder()
                        .name(name)
                        .birth(birth)
                        .phoneNumber(normalizedPhoneNumber)
                        .email(email)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .peanutCount(0)
                        .oauthProvider(null)
                        .build();
        }
}