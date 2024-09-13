package org.example.expert.domain.user.service.user;

import config.ServiceTestObjectFactory;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceGetTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void 조회_하고자하는_유저가_없을_경우_IRE를_반환한다() {
        // given
        long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.getUser(userId)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void 유저_조회에_성공한다() {
        // given
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserResponse userResponse = userService.getUser(userId);

        // then
        assertNotNull(userResponse);
        assertEquals(userResponse.getId(), user.getId());
        assertEquals(userResponse.getEmail(), user.getEmail());
    }
}