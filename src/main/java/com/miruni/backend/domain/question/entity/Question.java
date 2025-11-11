package com.miruni.backend.domain.question.entity;

import com.miruni.backend.domain.question.exception.QuestionErrorCode;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import com.miruni.backend.global.exception.BaseException;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "question")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "privacy_agreed", nullable = false)
    private boolean privacyAgreed;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Answer answer;

    public static Question create(final String title,
                                  final String content,
                                  final boolean privacyAgreed) {

        validateAgreed(privacyAgreed);

        return Question.builder()
                .title(title)
                .content(content)
                .privacyAgreed(privacyAgreed)
                .build();
    }

    private static void validateAgreed(boolean privacyAgreed) {
        if(!privacyAgreed) {
            throw BaseException.type(QuestionErrorCode.AGREEMENT_NOT_ACCEPT);
        }
    }

}
