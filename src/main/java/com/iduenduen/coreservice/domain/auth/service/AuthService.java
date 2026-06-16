package com.iduenduen.coreservice.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.security.JwtProvider;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.auth.dto.LoginRequest;
import com.iduenduen.coreservice.domain.auth.dto.LoginResponse;
import com.iduenduen.coreservice.domain.auth.dto.SignupRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupResponse;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getAccountNumber() == null
                || request.getName() == null || request.getBirthDate() == null || request.getRelation() == null
                || request.getRegion() == null || request.getChildCount() == null
                || request.getCertFileUrl() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        if (parentRepository.existsByEmail(request.getEmail())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_EMAIL);
        }
        if (parentRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_ACCOUNT_NUMBER);
        }

        Parent parent = Parent.builder()
                .email(request.getEmail())
                .accountNumber(request.getAccountNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .relation(request.getRelation())
                .region(request.getRegion())
                .childCount(request.getChildCount())
                .certFileUrl(request.getCertFileUrl())
                .build();

        Parent saved = parentRepository.save(parent);

        return SignupResponse.builder()
                .parentId(saved.getId())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getAccountNumber() == null || request.getPassword() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        Parent parent = parentRepository.findByAccountNumberAndDeletedAtIsNull(request.getAccountNumber())
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), parent.getPasswordHash())) {
            throw new GeneralException(ErrorStatus.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(parent.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .parentId(parent.getId())
                .build();
    }
}
