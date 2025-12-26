package com.miruni.backend.domain.user.repository;

import com.miruni.backend.domain.user.entity.Survey;
import com.miruni.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Survey findByUser(User user);
}




