package org.example.expert.domain.user.service.admin;


import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserAdminService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Mock
    private User user;

    @Test
    public void 권한을_변경하고자하는_유저가_없을_경우_IRE를_반환한다() {
        long userId = 1L;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userAdminService.changeUserRole(userId, userRoleChangeRequest);
        });

        // then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void 권한을_변경하는데_성공한다() {
        long userId = 1L;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        User mockTestUser = Mockito.mock(User.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockTestUser));

        // when
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        // then
        verify(mockTestUser, times(1)).updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}