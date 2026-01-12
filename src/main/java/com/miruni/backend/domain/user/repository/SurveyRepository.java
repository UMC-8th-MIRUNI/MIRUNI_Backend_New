package com.miruni.backend.domain.user.repository;

import com.miruni.backend.domain.user.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Survey findByUserId(Long userId);
}




