package com.iduenduen.coreservice.domain.parent.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.iduenduen.coreservice.common.config.SecurityConfig;
import com.iduenduen.coreservice.common.security.JwtAuthenticationFilter;
import com.iduenduen.coreservice.domain.parent.service.ParentService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(ParentController.class)
@Import(SecurityConfig.class)
class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParentService parentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2))
                    .doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    void getMe_미인증이면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/parents/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMe_미인증이면_401을_반환한다() throws Exception {
        mockMvc.perform(put("/parents/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateSelectedChild_미인증이면_401을_반환한다() throws Exception {
        mockMvc.perform(put("/parents/me/selected-child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"child_id\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateWizardProfile_미인증이면_401을_반환한다() throws Exception {
        mockMvc.perform(patch("/parents/me/wizard-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void holdings_인증된_사용자는_200을_반환한다() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/parents/me/holdings").with(authentication(auth)))
                .andExpect(status().isOk());
    }
}
