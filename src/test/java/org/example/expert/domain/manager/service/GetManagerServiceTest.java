package org.example.expert.domain.manager.service;

import org.example.expert.ServiceTestObjectFactory;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ManagerServiceGetTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void manage_목록_조회시_Todo가_없으면_NPE를_반환한다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> managerService.getManagers(todoId));
        assertEquals("Manager not found", exception.getMessage());
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        long userId = 1L;

        User user = ServiceTestObjectFactory.createUser(userId);
        Todo todo = ServiceTestObjectFactory.createTodo(user, todoId);
        Manager mockManager = ServiceTestObjectFactory.createManager(todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }
}