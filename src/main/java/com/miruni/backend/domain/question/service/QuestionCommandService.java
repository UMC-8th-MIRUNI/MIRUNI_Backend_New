package com.miruni.backend.domain.question.service;

import com.miruni.backend.domain.question.dto.command.SaveQuestionCommandDto;
import com.miruni.backend.domain.question.dto.command.UpdateQuestionCommandDto;
import com.miruni.backend.domain.question.dto.response.QuestionCommandResponseDto;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.entity.QuestionImage;
import com.miruni.backend.domain.question.exception.QuestionErrorCode;
import com.miruni.backend.domain.question.repository.QuestionRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCommandService {

    private final QuestionRepository questionRepository;
    private final S3Util s3Util;
    private final UserRepository userRepository;

    @Transactional
    public QuestionCommandResponseDto save(SaveQuestionCommandDto command) {

        List<MultipartFile> images = command.imageUrls();

        Question.validateImageCount(images != null ? images.size() : 0);

        User user = userRepository.findById(1L)
                .orElseThrow(IllegalArgumentException::new);

        Question question = questionRepository.save(
                Question.create(command.title(),
                        command.content(),
                        command.privacyAgreed(),
                        user)
        );

        uploadQuestionImages(question, images);

        return QuestionCommandResponseDto.from(question);
    }

    @Transactional
    public QuestionCommandResponseDto update(UpdateQuestionCommandDto command) {

        //나중에 userId와 같이 검증하기 리팩토링
        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> BaseException.type(QuestionErrorCode.QUESTION_NOT_FOUND));

        validateFinalImageCount(question, command);

        if (command.deleteImageIds() != null) {
            deleteImagesById(question, command.deleteImageIds());
        }

        if (command.imageUrls() != null) {
            uploadQuestionImages(question, command.imageUrls());
        }

        question.update(command.title(), command.content());
        return QuestionCommandResponseDto.from(question);


    }

    private void uploadQuestionImages(Question question, List<MultipartFile> images){
        if(images == null || images.isEmpty()){
            return;
        }

        for(MultipartFile image : images){
            String key = uploadImageToS3(question, image);
            QuestionImage questionImage = QuestionImage.create(question, key);
            question.addImage(questionImage);
        }
    }

    private String uploadImageToS3(Question question, MultipartFile image) {
        try {
            String directory = String.format("questions/%d/images", question.getId());
            return s3Util.uploadFile(image, directory);
        } catch (BaseException e) {
                    throw e;
        } catch (RuntimeException e) {
            log.error("질문 이미지 업로드 실패: questionId = {}", question.getId(), e);
            throw BaseException.type(QuestionErrorCode.FAILED_UPLOAD_IMAGE);
        }
    }

    private void validateFinalImageCount(Question question, UpdateQuestionCommandDto command) {
        int current = question.getImages().size();
        int delete = command.deleteImageIds() != null ? command.deleteImageIds().size() : 0;
        int add = command.imageUrls() != null ? command.imageUrls().size() : 0;
        Question.validateImageCount(current - delete + add);
    }


    private void deleteImagesById(Question question, List<Long> images) {
        List<QuestionImage> imagesToDelete = question.getImages().stream()
                .filter(img -> images.contains(img.getId()))
                .toList();

        if(imagesToDelete.size() != images.size()){
            throw BaseException.type(QuestionErrorCode.IMAGE_NOT_FOUND);
        }

        imagesToDelete.forEach(image -> {
            s3Util.deleteFile(image.getImageUrl());
        });
        question.removeAll(imagesToDelete);
        questionRepository.flush();
    }
}
