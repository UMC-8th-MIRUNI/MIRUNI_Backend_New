package com.miruni.backend.domain.fcm.controller;

import com.miruni.backend.domain.fcm.dto.request.RegisterTokenRequestDto;
import com.miruni.backend.domain.fcm.dto.response.RegisterTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    @PostMapping("/tokens")
    public RegisterTokenResponseDto registerToken(@RequestBody RegisterTokenRequestDto registerTokenRequestDto) {

    }
}
