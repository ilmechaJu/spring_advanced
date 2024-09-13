package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ServiceTestObjectFactory;
import config.TestWebConfig;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.service.CommentService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }
)
public class CommentControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @MockBean
    private CommentService commentService;
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
    void 댓글_작성_성공() throws Exception {
        // given
        long userId = 1L;
        long todoId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("Contents");
        String bodyContent = new ObjectMapper().writeValueAsString(commentSaveRequest);

        // when - then
        mockMvc.perform(post("/todos/{todoId}/comments", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(commentService, times(1)).saveComment(any(), eq(todoId), any());
    }

    @Test
    void 댓글_조회_성공() throws Exception {
        // given
        long userId = 1L;
        long todoId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(get("/todos/{todoId}/comments", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(commentService, times(1)).getComments(eq(todoId));
    }
}