package com.iduenduen.coreservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.auth.dto.LoginRequest;
import com.iduenduen.coreservice.domain.auth.dto.LoginResponse;
import com.iduenduen.coreservice.domain.auth.dto.PasswordResetRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupResponse;

import jakarta.validation.Valid;
import com.iduenduen.coreservice.domain.auth.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_201, authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, authService.login(request, response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }
        return ApiResponse.success(SuccessStatus.SUCCESS_200, authService.reissue(refreshToken));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        String accessToken = (authorization != null && authorization.startsWith("Bearer "))
                ? authorization.substring(7) : "";
        authService.logout(accessToken, refreshToken != null ? refreshToken : "", response);
        return ApiResponse.success(SuccessStatus.SUCCESS_200);
    }
}