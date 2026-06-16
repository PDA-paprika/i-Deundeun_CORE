package com.iduenduen.coreservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.iduenduen.coreservice.domain.auth.dto.SignupRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupResponse;
import com.iduenduen.coreservice.domain.auth.dto.TokenValidationResponse;
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

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validate(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        String accessToken = authorization.substring("Bearer ".length());
        Long parentId = authService.resolveActiveParentId(accessToken);

        TokenValidationResponse response = TokenValidationResponse.builder()
                .parentId(parentId)
                .build();
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }
}
