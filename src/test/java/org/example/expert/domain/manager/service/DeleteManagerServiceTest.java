package org.example.expert.domain.manager.service;

import org.example.expert.ManagerServiceObjectFactory;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.List;
import java.util.Optional;

import static org.example.expert.ManagerServiceObjectFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DeleteManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void testDeleteManager_유저가_없는_경우() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());
        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(userId, todoId, managerId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteManager_Todo가_없는_경우() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(new User()));
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(userId, todoId, managerId));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Nested
    class UserValidationTests {
        @Test
        public void testDeleteManager_유효하지_않은_유저가_할일을_삭제하려고_하는경우() {
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.of(new User()));
            given(todoRepository.findById(todoId)).willReturn(Optional.of(new Todo()));
            //given(todoRepository.findById(todoId).get()).willReturn(Optional.empty());

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(userId, todoId, managerId)
            );

            assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        public void testDeleteManager_유저와_todo유저가_같지않은_경우() {
            // given
            long userId = 1L;
            long todoId = 2L;
            long managerId = 1L;
            long anotherTodoId = 2L;


            User mockUser = ManagerServiceObjectFactory.createUser(userId); // 실제 요청을 한 유저 생성

            // todo에 다른 유저가 설정된 경우
            User anotherUser = ManagerServiceObjectFactory.createAnotherUser(anotherTodoId,"another@example.com"); // 다른 유저 생성
            // 다른 유저가 todo에 설정된 경우를 가정
            Todo todoWithDifferentUser = ManagerServiceObjectFactory.createTodo(anotherUser); // 생성자에서 todo의 소유자 설정


            // 유저는 존재하지만 Todo의 유저와 다를 때
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(todoRepository.findById(todoId)).willReturn(Optional.of(todoWithDifferentUser));

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.deleteManager(userId, todoId, managerId);
            });

            // 검증: 예외 메시지가 올바르게 나오는지 확인
            assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        public void testDeleteManager_유저와_todo유저가_같은_경우(){
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;

            User mockUser = createUser(userId); // 실제 요청을 한 유저 생성

            // 다른 유저가 todo에 설정된 경우를 가정
            Todo todoWithDifferentUser = new Todo("Sample Title", "Sample Contents", "Sunny", mockUser); // 생성자에서 todo의 소유자 설정


            // 유저는 존재하지만 Todo의 유저가 같을 때
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(todoRepository.findById(todoId)).willReturn(Optional.of(todoWithDifferentUser));

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.deleteManager(userId, todoId, managerId);
            });

            // 검증: 예외 메시지가 올바르게 나오는지 확인
            assertEquals("Manager not found", exception.getMessage());
        }
        @Test
        public void testDeleteManager_담당자의_todo에_등록된_id가_todo_id와_다른경우(){
            // given
            long userId = 1L;
            long todoId = 1L;
            long managerId = 1L;
            long anotherTodoId = 2L;

            User user = ManagerServiceObjectFactory.createUser(userId);
            Todo todo = ManagerServiceObjectFactory.createTodo(user, todoId);
            Todo anotherTodo = ManagerServiceObjectFactory.createTodo(user, anotherTodoId);
            Manager manager = ManagerServiceObjectFactory.createManager(anotherTodo);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));
            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                    () -> managerService.deleteManager(userId, todoId, managerId));

            // 검증: 예외 메시지가 올바르게 나오는지 확인
            assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
        }
        @Test
        public void 성공적으로_manager삭제를_한다() {
            long todoId = 1L;
            long userId = 1L;
            long managerId = 1L;

            User user = ManagerServiceObjectFactory.createUser(userId);
            Todo todo = ManagerServiceObjectFactory.createTodo(user, todoId);
            Manager manager = ManagerServiceObjectFactory.createManager(todo);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

            // when & then
            managerService.deleteManager(userId, todoId, managerId);

            verify(managerRepository, times(1)).delete(any());
        }

    }


    @Test
    public void manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> managerService.getManagers(todoId));
        assertEquals("Manager not found", exception.getMessage());
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

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
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

    @Test
        // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }
}
