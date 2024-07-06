package com.jbaacount.service;

import com.jbaacount.setup.MockSetup;
import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.model.Comment;
import com.jbaacount.model.type.CommentType;
import com.jbaacount.payload.request.comment.CommentCreateRequest;
import com.jbaacount.payload.request.comment.CommentUpdateRequest;
import com.jbaacount.payload.response.comment.CommentCreatedResponse;
import com.jbaacount.payload.response.comment.CommentMultiResponse;
import com.jbaacount.payload.response.comment.CommentUpdateResponse;
import com.jbaacount.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest extends MockSetup
{
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private UtilService authService;
    @Mock
    private VoteService voteService;

    @Mock
    private EntityManager em;

    @InjectMocks
    private CommentService commentService;

    @Test
    void saveComment()
    {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText(mockComment.getText());
        request.setPostId(mockPost.getId());

        given(postService.getPostById(any())).willReturn(mockPost);
        given(commentRepository.save(any(Comment.class))).willReturn(mockComment);


        // when
        CommentCreatedResponse response = commentService.saveComment(request, mockMember);
        System.out.println("response = " + response);

        // then
        assertThat(response.getText()).isEqualTo(mockComment.getText());
    }

    @DisplayName("save nested comment - 1 depth")
    @Test
    void saveNestedComment()
    {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText(mockComment.getText());
        request.setPostId(mockPost.getId());
        request.setParentCommentId(1L);

        Comment nestedComment = newMockComment(2L, "nested comment", mockPost, mockMember);

        given(postService.getPostById(any())).willReturn(mockPost);
        given(commentRepository.findById(any())).willReturn(Optional.of(mockComment));
        given(commentRepository.save(any(Comment.class))).willReturn(nestedComment);


        // when
        CommentCreatedResponse response = commentService.saveComment(request, mockMember);
        verify(postService, times(1)).getPostById(any());
        verify(commentRepository, times(1)).save(any(Comment.class));

        // then
        assertThat(response.getText()).isEqualTo("nested comment");

        verify(postService, times(1)).getPostById(any());
        verify(commentRepository, times(1)).findById(any());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("Should throw error when the depth is more than 2")
    @Test
    void saveNestedComment_depth2()
    {
        // given
        String text = "nested comment";

        CommentCreateRequest request = new CommentCreateRequest();
        request.setText(text);
        request.setPostId(mockPost.getId());
        request.setParentCommentId(2L);

        Comment nestedComment = newMockComment(2L, text, mockPost, mockMember);
        nestedComment.addParent(mockComment);

        Comment depth2Comment = newMockComment(3L, "nested comment", mockPost, mockMember);

        given(postService.getPostById(any())).willReturn(mockPost);
        given(commentRepository.findById(any())).willReturn(Optional.of(nestedComment));

        // when
        assertThrows(BusinessLogicException.class, () -> commentService.saveComment(request, mockMember));

        verify(postService, times(1)).getPostById(any());

        // then
        verify(postService, times(1)).getPostById(any());
        verify(commentRepository, times(1)).findById(any());
    }

    @Test
    void updateComment()
    {
        // given
        String text = "update comment";

        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setText(text);

        // when
        given(commentRepository.findById(any())).willReturn(Optional.of(mockComment));

        CommentUpdateResponse response = commentService.updateComment(request, 1L, mockMember);

        // then
        assertThat(response.getText()).isEqualTo(text);

        verify(commentRepository, times(1)).findById(any());
    }

    @Test
    void getComment()
    {
        // given
        given(commentRepository.findById(any())).willReturn(Optional.of(mockComment));

        // when
        Comment response = commentService.getComment(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getText()).isEqualTo(mockComment.getText());
        assertThat(response.getMember()).isEqualTo(mockMember);

        verify(commentRepository, times(1)).findById(any());
    }

    @DisplayName("Should throw Business Logic Exception Comment Not Found")
    @Test
    void getWrongComment()
    {
        // given

        // when
        assertThrows(BusinessLogicException.class, () -> commentService.getComment(2L));

        // then
        verify(commentRepository, times(1)).findById(any());
    }


    @Test
    void getAllCommentByPostId()
    {
        // given
        Comment comment2 = newMockComment(2L, "comment 2", mockPost, mockMember);
        Comment comment3 = newMockComment(3L, "comment 3", mockPost, mockMember);
        comment3.addParent(comment2);

        List<Comment> commentList = Arrays.asList(mockComment, comment2, comment3);

        // when
        given(commentRepository.findParentCommentsByPostId(mockPost.getId(), CommentType.PARENT_COMMENT.getCode()))
                .willReturn(commentList);

        List<CommentMultiResponse> response = commentService.getAllCommentByPostId(mockPost.getId(), mockMember);

        // then
        assertThat(response.get(0).getText()).isEqualTo(mockComment.getText());
        assertThat(response.get(0).getChildren()).isEmpty();

        assertThat(response.get(1).getText()).isEqualTo(comment2.getText());
        assertThat(response.get(1).getChildren().get(0).getText()).isEqualTo(comment3.getText());

        assertThat(response.get(2).getChildren()).isEmpty();

        verify(commentRepository, times(1)).findParentCommentsByPostId(mockPost.getId(), CommentType.PARENT_COMMENT.getCode());
    }


    @Test
    void deleteTest_noChildrenComment()
    {
        // given
        given(commentRepository.findById(any())).willReturn(Optional.of(mockComment));

        // when
        boolean result = commentService.deleteComment(mockComment.getId(), mockMember);

        // then
        assertThat(result).isTrue();
        verify(commentRepository, times(1)).deleteById(mockComment.getId());
    }

    @DisplayName("Should not be deleted. Instead isRemoved should be true")
    @Test
    void deleteTest_hasChildrenComment()
    {
        // given
        Comment nestedComment = newMockComment(2L, "nested comment", mockPost, mockMember);
        nestedComment.addParent(mockComment);

        given(commentRepository.findById(any())).willReturn(Optional.of(mockComment));

        // when
        boolean result = commentService.deleteComment(mockComment.getId(), mockMember);

        // then
        assertThat(result).isTrue();
        assertThat(mockComment.getIsRemoved()).isTrue();
        verify(commentRepository, never()).deleteById(mockComment.getId());
    }
}