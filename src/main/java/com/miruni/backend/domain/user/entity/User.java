package com.miruni.backend.domain.user.entity;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Builder.Default
    private ProfileImage profileImage = ProfileImage.GREEN;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Agreement> agreements = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BasicPlan> basicPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FcmToken> fcmTokens = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isSocialUser() {
        return this.oauthProvider != null;
    }

    // ===== 소프트 삭제 관련 로직 (User 전용) =====

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
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
     * 소셜 로그인 신규 유저 (가입 미완료: ROLE_GUEST)
     */
    public static User createSocialGuest(String name, String email, String encodedPassword, String nickname, OauthProvider provider) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .oauthProvider(provider)
                .role(UserRole.PENDING_SIGNUP)
                .build();
    }
}
