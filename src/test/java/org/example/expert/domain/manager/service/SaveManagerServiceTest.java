package org.example.expert.domain.manager.service;

import org.example.expert.ServiceTestObjectFactory;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SaveManagerServiceTest {
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    void manager를_성공적으로_등록한다() {
        // given
        long todoId = 1L;
        long managerUserId = 2L;
        long userId = 1L;

        AuthUser authUser = ServiceTestObjectFactory.createAuthUser(userId);
        Todo todo = ServiceTestObjectFactory.createTodo(User.fromAuthUser(authUser));
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);
        User managerUser = ServiceTestObjectFactory.createUser(managerUserId);
        Manager manager = new Manager(managerUser, todo);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any())).willReturn(manager);

        // when & then
        ManagerSaveResponse managerResponse = managerService.saveManager(authUser, todoId, managerSaveRequest);
        // ManagerResponse가 반환된다.
        assertNotNull(managerResponse);
        // 등록된 Manager의 userId와 등록전 Manager의 userId가 동일한지 확인한다.
        assertEquals(managerResponse.getUser().getId(), managerUser.getId());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;


        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void UserId와_ManagerId가_동일할_경우_IRE_예외가_발생한다() {
        long todoId = 1L;
        long managerUserId = 1L;
        long userId = 1L;

        AuthUser authUser = ServiceTestObjectFactory.createAuthUser(userId);
        Todo todo = ServiceTestObjectFactory.createTodo(User.fromAuthUser(authUser));
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);
        User managerUser = ServiceTestObjectFactory.createUser(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
    }

}