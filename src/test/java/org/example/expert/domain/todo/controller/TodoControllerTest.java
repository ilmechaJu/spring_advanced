package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ServiceTestObjectFactory;
import config.TestWebConfig;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
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

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {TodoController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }
)
public class TodoControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @MockBean
    private TodoService todoService;
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @BeforeEach
    public void setUp() {
        mockMvc = TestWebConfig.setFilter(wac);
    }

    private void userSetup(User user) {
        mockPrincipal = TestWebConfig.userSetup(user);
    }


    @Test
    void Todo_작성_성공() throws Exception {
        // given
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("Title", "Contents");
        String bodyContent = new ObjectMapper().writeValueAsString(todoSaveRequest);

        // when - then
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(todoService, times(1)).saveTodo(any(), any());
    }

    @Test
    void Todo_페이징_조회_성공() throws Exception {
        // given
        int page = 1;
        int size = 10;
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(get("/todos?page={page}&size={size}", page, size)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(todoService, times(1)).getTodos(page, size);
    }

    @Test
    void Todo_조회_성공() throws Exception {
        // given
        long userId = 1L;
        long todoId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(get("/todos/{todoId}", todoId)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(todoService, times(1)).getTodo(todoId);
    }
}