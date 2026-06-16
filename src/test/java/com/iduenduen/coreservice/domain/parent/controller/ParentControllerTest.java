package com.iduenduen.coreservice.domain.parent.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.iduenduen.coreservice.domain.parent.service.ParentService;

@WebMvcTest(ParentController.class)
class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParentService parentService;

    @Test
    void getMe_미인증이면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/parents/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMe_함수는_인증_미구현으로_500을_반환한다() throws Exception {
        mockMvc.perform(put("/parents/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void updateSelectedChild_함수는_인증_미구현으로_500을_반환한다() throws Exception {
        mockMvc.perform(put("/parents/me/selected-child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"child_id\":\"child-1\"}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void updateWizardProfile_함수는_인증_미구현으로_500을_반환한다() throws Exception {
        mockMvc.perform(patch("/parents/me/wizard-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void holdings_함수는_인증과_무관하게_200을_반환한다() throws Exception {
        mockMvc.perform(get("/parents/me/holdings"))
                .andExpect(status().isOk());
    }
}
