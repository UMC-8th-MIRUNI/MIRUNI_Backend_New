package com.miruni.backend.domain.user.entity;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.common.BaseEntity;
import com.miruni.backend.global.exception.BaseException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20, unique = true)
    private String nickname;

    @Column(name = "peanut_count", nullable = false)
    @Builder.Default
    private int peanutCount = 0;

    @Column(name = "remain_count", nullable = false)
    @Builder.Default
    private int remainChance = 3;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_image")
    @Builder.Default
    private ProfileImage profileImage = ProfileImage.GREEN;

    @OneToMany(mappedBy = "user")
    private List<Agreement> agreements = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Survey> surveys = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasicPlan> basicPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    /**
     * 일반 회원가입용 팩토리 메서드
     */
    public static User create(
            String name,
            String rawBirthDate,
            String rawPhoneNumber,
            String email,
            String encodedPassword,
            String nickname
    ) {
        LocalDate birth = LocalDate.parse(rawBirthDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        String normalizedPhoneNumber = rawPhoneNumber.replace("-", "");

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

    /**
     * 소셜 로그인 사용자인지 확인
     */
    public boolean isSocialUser() {
        return this.oauthProvider != null;
    }
    
    /**
     * 비밀번호가 설정되어 있는지 확인
     */
    public boolean hasPassword() {
        return this.password != null && !this.password.isBlank();
    }
    
    /**
     * 비밀번호 업데이트
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void addPeanuts(int count) {
        this.peanutCount += count;
    }
    public void deductAiChance(){
        if(this.remainChance > 0){
            this.remainChance--;
        }else {
            throw BaseException.type(UserErrorCode.NOT_ENOUGH_POINT);
        }
    }

    public void tryRecharge(){
        final int PEANUT_RECHARGE_PRICE = 30;

        if(this.peanutCount >= PEANUT_RECHARGE_PRICE){
            this.peanutCount -= PEANUT_RECHARGE_PRICE;
            this.remainChance += 1;
        }
    }


    public void updateProfile(ProfileImage profileImage, String nickname) {
        this.profileImage = profileImage;
        this.nickname = nickname;
    }

    public void updateUserInfo(String name, LocalDate birth, String phoneNumber, String email) {
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
