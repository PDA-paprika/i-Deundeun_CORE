package com.iduenduen.coreservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.auth.dto.ConfirmEmailVerificationRequest;
import com.iduenduen.coreservice.domain.auth.dto.EmailVerificationRequest;
import com.iduenduen.coreservice.domain.auth.service.EmailVerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody @Valid EmailVerificationRequest request) {
        emailVerificationService.requestEmailVerificationCode(request.email());
        return ApiResponse.success(SuccessStatus.EMAIL_SEND_SUCCESS);
    }

    @PostMapping("/send/password-reset")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetCode(@RequestBody @Valid EmailVerificationRequest request) {
        emailVerificationService.requestPasswordResetCode(request.email());
        return ApiResponse.success(SuccessStatus.EMAIL_SEND_SUCCESS);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody @Valid ConfirmEmailVerificationRequest request) {
        emailVerificationService.confirmEmailVerificationCode(request.email(), request.emailVerificationCode());
        return ApiResponse.success(SuccessStatus.EMAIL_VERIFY_SUCCESS);
    }
}
