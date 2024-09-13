package org.example.expert.domain.user.service.user;

import config.ServiceTestObjectFactory;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.config.utility.PasswordUtil;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceChangePasswordTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordUtil passwordUtil;

    @Test
    public void 새로운_비밀번호가_형식에_어긋난_경우_IRE를_반환한다() {
        // given
        long userId = 1L;
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("Aa123456", "1234");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest)
        );

        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

    @Test
    public void  비밀번호를_바꿀_유저가_존재하지_않으면_IRE를_반환한다() {
        // given
        long userId = 1L;
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("Aa123456", "Aa123456");

        given(passwordUtil.isValidPassword(userChangePasswordRequest.getNewPassword())).willReturn(true);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void 비밀번호를_바꾸기_전과_동일하게_변경하면_IRE를_반환한다() {
        // given
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("Aa123456", "Aa123456");

        given(passwordUtil.isValidPassword(userChangePasswordRequest.getNewPassword())).willReturn(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())).willReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest)
        );

        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 기존_비밀번호와_Old비밀번호가_다를_경우_IRE를_반환한다() {
        // given
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1234", "Aa123456");

        given(passwordUtil.isValidPassword(userChangePasswordRequest.getNewPassword())).willReturn(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest)
        );

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void 비밀번호_변경에_성공한다() {
        // given
        long userId = 1L;

        User user = ServiceTestObjectFactory.createUser(userId);
        user = Mockito.mock(User.class);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1234", "Aa123456");

        given(passwordUtil.isValidPassword(userChangePasswordRequest.getNewPassword())).willReturn(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(true);

        userService.changePassword(userId, userChangePasswordRequest);

        verify(user, times(1)).changePassword(any());
    }
}