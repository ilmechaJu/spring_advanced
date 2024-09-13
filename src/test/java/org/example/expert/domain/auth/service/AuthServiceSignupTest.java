package org.example.expert.domain.auth.service;

import config.ServiceTestObjectFactory;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceSignupTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    public void 이메일이_중복될경우_IRE를_반환한다() {
        SignupRequest signupRequest = new SignupRequest("a@b.com", "1234", "ADMIN");

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> authService.signup(signupRequest));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    public void 회원가입에_성공한다() {
        long userId = 1L;
        SignupRequest signupRequest = new SignupRequest("a@b.com", "1234", "ADMIN");
        User user = ServiceTestObjectFactory.createUser(userId);

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        SignupResponse signupResponse = authService.signup(signupRequest);

        assertNotNull(signupResponse);
    }
}