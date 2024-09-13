package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.TestWebConfig;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {AuthController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }
)
public class AuthControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = TestWebConfig.setFilter(wac);
    }

    @Test
    void 회원가입을_요청한다() throws Exception {
        SignupRequest signupRequest = new SignupRequest("a@b.com", "1234", "ADMIN");
        String bodyContent = new ObjectMapper().writeValueAsString(signupRequest);

        // when - then
        mockMvc.perform(post("/auth/signup")
                        // RequestBody Type은 JSON 형태로 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        // 내용
                        .content(bodyContent))
                .andExpect(status().isOk())
                .andDo(print());

        verify(authService, times(1)).signup(any());
    }

    @Test
    void 로그인을_요청한다() throws Exception {
        SigninRequest signinRequest = new SigninRequest("a@b.com", "1234");
        String bodyContent = new ObjectMapper().writeValueAsString(signinRequest);

        // when - then
        mockMvc.perform(post("/auth/signin")
                        // RequestBody Type은 JSON 형태로 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        // 내용
                        .content(bodyContent))
                .andExpect(status().isOk())
                .andDo(print());

        verify(authService, times(1)).signin(any());
    }
}
