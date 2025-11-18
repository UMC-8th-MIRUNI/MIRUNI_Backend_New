package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }
}
