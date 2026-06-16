package com.iduenduen.coreservice.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.security.JwtProvider;
import com.iduenduen.coreservice.domain.auth.dto.LoginRequest;
import com.iduenduen.coreservice.domain.auth.dto.SignupRequest;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest createSignupRequest() {
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "password", "mypassword123");
        ReflectionTestUtils.setField(request, "accountNumber", "1234567890");
        ReflectionTestUtils.setField(request, "name", "홍길동");
        ReflectionTestUtils.setField(request, "birthDate", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(request, "relation", "MOTHER");
        ReflectionTestUtils.setField(request, "region", "서울");
        ReflectionTestUtils.setField(request, "childCount", 1);
        ReflectionTestUtils.setField(request, "certFileUrl", "https://example.com/cert.pdf");
        return request;
    }

    private Parent createParent() {
        Parent parent = Parent.builder()
                .email("test@example.com")
                .accountNumber("1234567890")
                .passwordHash("encoded-password")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .relation("MOTHER")
                .region("서울")
                .childCount(1)
                .certFileUrl("https://example.com/cert.pdf")
                .build();
        ReflectionTestUtils.setField(parent, "id", 1L);
        return parent;
    }

    @Test
    void signup_성공() {
        SignupRequest request = createSignupRequest();
        given(parentRepository.existsByEmail("test@example.com")).willReturn(false);
        given(parentRepository.existsByAccountNumber("1234567890")).willReturn(false);
        given(passwordEncoder.encode("mypassword123")).willReturn("encoded-password");
        given(parentRepository.save(org.mockito.ArgumentMatchers.any(Parent.class))).willReturn(createParent());

        var response = authService.signup(request);

        assertThat(response.getParentId()).isEqualTo(1L);
    }

    @Test
    void signup_이메일이_중복되면_예외() {
        SignupRequest request = createSignupRequest();
        given(parentRepository.existsByEmail("test@example.com")).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void signup_계좌번호가_중복되면_예외() {
        SignupRequest request = createSignupRequest();
        given(parentRepository.existsByEmail("test@example.com")).willReturn(false);
        given(parentRepository.existsByAccountNumber("1234567890")).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void signup_필수값이_없으면_예외() {
        SignupRequest request = new SignupRequest();

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void login_성공() {
        Parent parent = createParent();
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "accountNumber", "1234567890");
        ReflectionTestUtils.setField(request, "password", "mypassword123");

        given(parentRepository.findByAccountNumberAndDeletedAtIsNull("1234567890")).willReturn(Optional.of(parent));
        given(passwordEncoder.matches("mypassword123", "encoded-password")).willReturn(true);
        given(jwtProvider.createAccessToken(1L)).willReturn("access-token");

        var response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getParentId()).isEqualTo(1L);
    }

    @Test
    void login_존재하지_않는_계좌번호면_예외() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "accountNumber", "0000000000");
        ReflectionTestUtils.setField(request, "password", "mypassword123");

        given(parentRepository.findByAccountNumberAndDeletedAtIsNull("0000000000")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void login_비밀번호가_틀리면_예외() {
        Parent parent = createParent();
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "accountNumber", "1234567890");
        ReflectionTestUtils.setField(request, "password", "wrong-password");

        given(parentRepository.findByAccountNumberAndDeletedAtIsNull("1234567890")).willReturn(Optional.of(parent));
        given(passwordEncoder.matches("wrong-password", "encoded-password")).willReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void login_필수값이_없으면_예외() {
        LoginRequest request = new LoginRequest();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void resolveActiveParentId_성공() {
        Parent parent = createParent();
        given(jwtProvider.getParentId("valid-token")).willReturn(1L);
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));

        Long parentId = authService.resolveActiveParentId("valid-token");

        assertThat(parentId).isEqualTo(1L);
    }

    @Test
    void resolveActiveParentId_토큰이_위조되었으면_예외() {
        given(jwtProvider.getParentId("invalid-token"))
                .willThrow(new JwtProvider.InvalidTokenException(new RuntimeException("bad signature")));

        assertThatThrownBy(() -> authService.resolveActiveParentId("invalid-token"))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void resolveActiveParentId_탈퇴한_사용자면_예외() {
        given(jwtProvider.getParentId("withdrawn-user-token")).willReturn(1L);
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resolveActiveParentId("withdrawn-user-token"))
                .isInstanceOf(GeneralException.class);
    }
}
