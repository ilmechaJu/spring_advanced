package org.example.expert.domain.comment.service;

import org.example.expert.ServiceTestObjectFactory;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {
    @InjectMocks
    private CommentAdminService commentAdminService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    public void CommentAdminService에서_Comment를_삭제할_경우_deleteById가_호출된다() {
        //given
        long userId = 2L;
        long commentId = 1L;

        Comment testComment = ServiceTestObjectFactory.createComment(userId, commentId);

        // when
        commentAdminService.deleteComment(testComment.getId());

        // then
        Mockito.verify(commentRepository, times(1)).deleteById(anyLong());
    }
}