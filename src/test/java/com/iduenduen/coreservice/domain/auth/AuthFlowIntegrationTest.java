package com.iduenduen.coreservice.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입_로그인_탈퇴_전체_흐름() throws Exception {
        String signupRequest = """
                {
                  "email": "flow-test@example.com",
                  "password": "mypassword123",
                  "account_number": "9999999999",
                  "name": "홍길동",
                  "birth_date": "1990-01-01",
                  "relation": "MOTHER",
                  "region": "서울",
                  "child_count": 1,
                  "cert_file_url": "https://example.com/cert.pdf"
                }
                """;

        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated())
                .andReturn();

        Long parentId = readLong(signupResult, "$.data.parent_id");
        assertThat(parentId).isPositive();

        String loginRequest = """
                {
                  "email": "flow-test@example.com",
                  "password": "mypassword123"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.data.access_token");

        assertThat(accessToken).isNotBlank();

        // 탈퇴 전: 발급받은 토큰으로 내 정보 조회 성공
        mockMvc.perform(get("/parents/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/parents/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // 탈퇴 후: 동일 토큰으로 조회 시 계정이 존재하지 않아 404
        mockMvc.perform(get("/parents/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    private Long readLong(MvcResult result, String jsonPath) throws Exception {
        Number value = JsonPath.read(result.getResponse().getContentAsString(), jsonPath);
        return value.longValue();
    }
}
