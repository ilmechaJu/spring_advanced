package org.example.expert.domain.todo.service;

import config.ServiceTestObjectFactory;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceSaveTest {
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private TodoService todoService;
    @Mock
    WeatherClient weatherClient;

    @Test
    public void Todo에_성공적으로_데이터를_넣는다() {
        // given
        long userId = 1L;
        long todoId = 1L;

        AuthUser authUser = ServiceTestObjectFactory.createAuthUser(userId);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        Todo todo = ServiceTestObjectFactory.createTodo(todoSaveRequest, User.fromAuthUser(authUser), todoId);

        given(todoRepository.save(any())).willReturn(todo);
        given(weatherClient.getTodayWeather()).willReturn("Sunny");

        // when & then
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);
        assertNotNull(todoSaveResponse);
    }
}