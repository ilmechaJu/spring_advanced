package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ServiceTestObjectFactory;
import config.TestWebConfig;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.security.Principal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {ManagerController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }

)

public class ManagerControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @MockBean
    private JwtUtil jwtUtil; //?
    @MockBean
    private ManagerService managerService;
//    @InjectMocks
//    private ManagerController managerController;

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
    void 매니저_등록에_성공한다() throws Exception {
        // given
        long todoId = 1L;
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(todoId);
        String bodyContent = new ObjectMapper().writeValueAsString(managerSaveRequest);

        // when - then
        mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyContent)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(managerService, times(1)).saveManager(any(), eq(todoId), any());
    }

    @Test
    void 해당_스케쥴에_참여중인_매니저들의_정보조회를_성공한다() throws Exception {
        // given
        long todoId = 1L;
        long userId = 1L;
        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(get("/todos/{todoId}/managers", todoId)
                                .requestAttr(
                                        "userId", user.getId() //http request   설정
                                )
                                .requestAttr("email", user.getEmail())
                                .requestAttr("userRole", user.getUserRole())
                )
                        // 유저 정보
                .andExpect(status().isOk()) //정보 담아두는 곳
                .andDo(print());

        verify(managerService, times(1)).getManagers(eq(todoId));
    }

    @Test
    void 스케쥴에_참여중인_매니저를_제외한다() throws Exception {
        // given
        long todoId = 1L;
        long managerId = 1L;
        long userId = 2L;

        User user = ServiceTestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId, managerId)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(managerService, times(1)).deleteManager(userId, todoId, managerId);
    }
}


