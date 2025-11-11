package com.miruni.backend.domain.question.service;

import com.miruni.backend.domain.question.dto.command.SaveQuestionCommandDto;
import com.miruni.backend.domain.question.dto.response.QuestionResponseDto;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.exception.QuestionErrorCode;
import com.miruni.backend.domain.question.repository.QuestionRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCommandService {

    private final QuestionRepository questionRepository;
    private final S3Util s3Util;

    public QuestionResponseDto save(SaveQuestionCommandDto command) {

        Question question = Question.create(command.title(), command.content(), command.privacyAgreed());

        questionRepository.save(question);


        return QuestionResponseDto.from(question);
    }

    private void uploadProfileImages(User user, List<MultipartFile> profileImages) {
        if(profileImages != null && !profileImages.isEmpty()) {
            for (MultipartFile profileImage : profileImages) {
                String key;
                try {
                    key = s3Util.uploadFile(profileImage, "users/" + user.getId() + "/profiles");
                } catch (BaseException e) {
                    throw e;
                } catch (RuntimeException e) {
                    log.error("프로필 이미지 업로드 실패: userId = {}", user.getId(), e);
                    throw BaseException.type(QuestionErrorCode.FAILED_UPLOAD_IMAGE);
                }
            }
        }
    }
}
