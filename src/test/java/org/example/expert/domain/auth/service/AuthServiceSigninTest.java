package org.example.expert.domain.auth.service;

import config.ServiceTestObjectFactory;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceSigninTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    public void 로그인시_존재하지않는_이메일을_입력할경우_IRE를_반환한다() {
        SigninRequest signinRequest = new SigninRequest("a@b.com", "1234");

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> authService.signin(signinRequest));

        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void 로그인시_비밀번호가_일치하지_않으면_AuthException을_반환한다() {
        long userId = 1L;

        SigninRequest signinRequest = new SigninRequest("a@b.com", "1234");
        User user = ServiceTestObjectFactory.createUser(userId);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(false);

        AuthException exception = assertThrows(AuthException.class,
                () -> authService.signin(signinRequest)
        );

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void 로그인에_성공_한다() {
        long userId = 1L;

        SigninRequest signinRequest = new SigninRequest("a@b.com", "1234");
        User user = ServiceTestObjectFactory.createUser(userId);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(true);

        SigninResponse signinResponse = authService.signin(signinRequest);

        assertNotNull(signinResponse);
    }
}
