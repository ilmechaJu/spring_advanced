package org.example.expert;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.util.ReflectionTestUtils;


// 객체 외주로 만들기
public class ManagerServiceObjectFactory {

    public static AuthUser createAuthUser(Long id) {
        return new AuthUser(id, "email", UserRole.USER);
    }

    public static User createUsr(Long id){
        return User.fromAuthUser(createAuthUser(id));
    }

    public static Todo createTodo(User user){
        return new Todo("Sample Title","Sample Contents","weather", user);
    }

    public static Todo createTodo(User user, Long todoId){
        Todo todo = createTodo(user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        return todo;
    }

    public static User createUser(Long userId){
        return new User(userId, "user@example.com", UserRole.USER);
    }
    public static User createAnotherUser(Long userId, String email){
        return new User(userId, email, UserRole.USER);
    }
    public static Manager createManager(Todo todo) {
        return new Manager(todo.getUser(), todo);
    }


    public static Comment createComment(long userId) {
        User user = createUser(userId);
        Todo todo = createTodo(user);

        return new Comment("contents", user, todo);
    }

    public static Comment createComment(long userId, long commentId) {
        Comment comment = createComment(userId);
        ReflectionTestUtils.setField(comment, "id", commentId);

        return comment;
    };
}

