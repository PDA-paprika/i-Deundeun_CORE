package com.iduenduen.coreservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.auth.dto.LoginRequest;
import com.iduenduen.coreservice.domain.auth.dto.LoginResponse;
import com.iduenduen.coreservice.domain.auth.dto.SignupRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupResponse;
import com.iduenduen.coreservice.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(SuccessStatus.SUCCESS_201, response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }
}
