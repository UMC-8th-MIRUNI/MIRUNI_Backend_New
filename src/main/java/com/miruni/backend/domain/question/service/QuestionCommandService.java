package com.miruni.backend.domain.question.service;

import com.miruni.backend.domain.question.dto.command.SaveQuestionCommandDto;
import com.miruni.backend.domain.question.dto.response.QuestionResponseDto;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.entity.QuestionImage;
import com.miruni.backend.domain.question.exception.QuestionErrorCode;
import com.miruni.backend.domain.question.repository.QuestionRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCommandService {

    private final QuestionRepository questionRepository;
    private final S3Util s3Util;

    @Transactional
    public QuestionResponseDto save(SaveQuestionCommandDto command) {

        LocalDate localDate = LocalDate.now();

        User user = User.create("추상윤", "dhzkltdh@gmail.com", localDate,
                "010-7689-3141", "tkddbs3535", "chuchu");

        Question question = questionRepository.save(
                Question.create(command.title(),
                        command.content(),
                        command.privacyAgreed(),
                        user)
        );

        uploadQuestionImages(question, command.imageUrls());

        return QuestionResponseDto.from(question);
    }

    private void uploadQuestionImages(Question question, List<MultipartFile> images){
        if(images == null || images.isEmpty()){
            return;
        }

        for(MultipartFile image : images){
            String key = uploadImageToS3(question, image);
            QuestionImage questionImage = QuestionImage.create(question, key);
            question.getImages().add(questionImage);
        }
    }


    private String uploadImageToS3(Question question, MultipartFile image) {
        try {
            String directory = String.format("questions/%d/images", question.getId());
            return s3Util.uploadFile(image, directory);
        } catch (BaseException e) {
                    throw e;
        } catch (RuntimeException e) {
            log.error("질문 이미지 업로드 실패: userId = {}", question.getId(), e);
            throw BaseException.type(QuestionErrorCode.FAILED_UPLOAD_IMAGE);
        }
    }
}
