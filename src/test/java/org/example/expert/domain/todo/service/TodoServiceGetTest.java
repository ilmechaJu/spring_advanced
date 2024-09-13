package org.example.expert.domain.todo.service;

import config.ServiceTestObjectFactory;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceGetTest {
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private TodoService todoService;

    @Test
    public void Todo조회시_Todo가_없으면_IRE를_반환한다() {
        // given
        long todoId = 1L;

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                todoService.getTodo(todoId)
        );

        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void Todo조회를_성공한다() {
        // given
        long todoId = 1L;
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);
        Todo todo = ServiceTestObjectFactory.createTodo(user);

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        // when & then
        TodoResponse todoResponse = todoService.getTodo(todoId);

        assertNotNull(todoResponse);
    }

    @Test
    public void Todo를_Paging해서_조회한다() {
        // given
        long userId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);
        User user = ServiceTestObjectFactory.createUser(userId);
        Todo todo = ServiceTestObjectFactory.createTodo(user);

        Page<Todo> todos = new PageImpl<>(List.of(
                ServiceTestObjectFactory.createTodo(user),
                ServiceTestObjectFactory.createTodo(user),
                ServiceTestObjectFactory.createTodo(user),
                ServiceTestObjectFactory.createTodo(user)
        ));

        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todos);

        // when & then
        Page<TodoResponse> pageTodoResponse = todoService.getTodos(page, size);

        assertNotNull(pageTodoResponse);
    }
}