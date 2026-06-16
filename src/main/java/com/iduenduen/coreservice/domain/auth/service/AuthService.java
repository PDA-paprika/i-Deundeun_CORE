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
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        Parent parent = parentRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
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

    /**
     * 토큰 서명/만료를 검증하고, 탈퇴(deleted_at) 여부까지 함께 확인한다.
     * JWT는 stateless라 발급된 토큰 자체를 지울 수 없으므로, 검증 시점에 현재 DB 상태를 다시 확인해
     * 탈퇴한 사용자의 토큰은 즉시 거부되도록 한다.
     */
    public Long resolveActiveParentId(String accessToken) {
        Long parentId;
        try {
            parentId = jwtProvider.getParentId(accessToken);
        } catch (JwtProvider.InvalidTokenException e) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        return parentRepository.findByIdAndDeletedAtIsNull(parentId)
                .map(Parent::getId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNAUTHORIZED));
    }
}
