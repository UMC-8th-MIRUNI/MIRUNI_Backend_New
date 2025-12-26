package com.miruni.backend.domain.user.entity;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_image")
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "user")
    private List<Agreement> agreements = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Survey survey;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasicPlan> basicPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    // ===== 비즈니스 로직 =====

    public void addPeanuts(int count) {
        this.peanutCount += count;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public Survey getSurvey() {
        return this.survey;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 일반 회원가입용 USER 생성
     */
    public static User createNormalUser(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .peanutCount(0)
                .role(UserRole.USER)
                .build();
    }

    /**
     * 소셜 로그인 신규 유저 (ROLE_GUEST)
     */
    public static User createSocialGuest(String name, String email, String encodedPassword, String nickname, OauthProvider provider) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .oauthProvider(provider)
                .role(UserRole.GUEST)
                .build();
    }
}
