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
@Table(name = "question")
public class Question extends BaseEntity {

    private static final int MAX_IMAGE_COUNT = 5;

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

    @Builder
    private Question(final String title,
                     final String content,
                     final boolean privacyAgreed,
                     final User user){
        this.title = title;
        this.content = content;
        this.privacyAgreed = privacyAgreed;
        this.user = user;
        this.images = new ArrayList<>();
    }

    public static Question create(final String title,
                                  final String content,
                                  final boolean privacyAgreed,
                                  final User user) {

        validateAgreed(privacyAgreed);

        return Question.builder()
                .title(title)
                .content(content)
                .privacyAgreed(true)
                .user(user)
                .build();
    }

    public void update(final String title,
                               final String content){
        this.title = title;
        this.content = content;
    }


    public void addImage(QuestionImage image){
        this.images.add(image);
    }

    public void removeImage(QuestionImage image){
        this.images.remove(image);
    }

    private static void validateAgreed(boolean privacyAgreed) {
        if(!privacyAgreed) {
            throw BaseException.type(QuestionErrorCode.AGREEMENT_NOT_ACCEPT);
        }
    }

    public static void validateImageCount(int count){
        if(count > MAX_IMAGE_COUNT){
            throw BaseException.type(QuestionErrorCode.EXCEED_MAX_IMAGE_COUNT);
        }
    }

}
