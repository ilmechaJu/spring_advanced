package org.example.expert.domain.manager.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.manager.service.ManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(
        controllers = {ManagerController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfigurer.class
                )
        }

)

@ExtendWith(MockitoExtension.class)
public class ManagerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private JwtUtil jwtUtil; //?
    @MockBean
    private ManagerService managerService;
    @InjectMocks
    private ManagerController managerController;

    @Test
    public void 매니저_조회() throws Exception { //@GetMapping 해보자
        long todoId = 1L;
        given(managerService.getManagers(todoId)).willReturn(List.of());

        // when-then
        mockMvc.perform(get("/todos/{todoId}/managers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
}


