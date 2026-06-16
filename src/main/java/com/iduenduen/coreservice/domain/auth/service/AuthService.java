package com.iduenduen.coreservice.domain.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

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
        return SignupResponse.builder().parentId(saved.getId()).build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        Parent parent = parentRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), parent.getPasswordHash())) {
            throw new GeneralException(ErrorStatus.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(parent.getId());
        String refreshToken = jwtProvider.createRefreshToken(parent.getId());

        redisTemplate.opsForValue().set(
                "refresh:" + parent.getId(),
                refreshToken,
                jwtProvider.getRefreshTokenExpirationMs(),
                TimeUnit.MILLISECONDS
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // prod 환경에서는 true로 변경 필요
                .path("/")
                .maxAge(jwtProvider.getRefreshTokenExpirationMs() / 1000)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String reissue(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        Long parentId = jwtProvider.getParentId(refreshToken);
        String savedToken = redisTemplate.opsForValue().get("refresh:" + parentId);

        if (!refreshToken.equals(savedToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        return jwtProvider.createAccessToken(parentId);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken, HttpServletResponse response) {
        if (jwtProvider.isValid(refreshToken)) {
            Long parentId = jwtProvider.getParentId(refreshToken);
            redisTemplate.delete("refresh:" + parentId);
        }

        if (jwtProvider.isValid(accessToken)) {
            long remaining = jwtProvider.getRemainingExpiration(accessToken);
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    "logout",
                    remaining,
                    TimeUnit.MILLISECONDS
            );
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

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