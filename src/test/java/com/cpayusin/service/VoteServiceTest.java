package com.cpayusin.service;

import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.infrastructure.Vote;
import com.cpayusin.vote.service.VoteServiceImpl;
import com.cpayusin.setup.MockSetup;
import com.cpayusin.vote.service.port.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest extends MockSetup
{
    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private VoteServiceImpl voteService;

    @Test
    void votePost()
    {
        // given
        given(postRepository.findByIdWithOptimisticLock(any())).willReturn(Optional.of(mockPost));
        given(voteRepository.findByMemberIdAndPostId(mockMember2.getId(), mockPost.getId())).willReturn(Optional.empty());
        given(voteRepository.save(any())).willReturn(postVote);

        // when
        boolean result = voteService.votePost(mockMember2, mockPost.getId());

        // then
        assertThat(result).isTrue();
        assertThat(mockPost.getVoteCount()).isEqualTo(1);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void voteComment()
    {
        // given
        given(commentRepository.findByIdWithOptimisticLock(any())).willReturn(Optional.of(mockComment));
        given(voteRepository.findByMemberIdAndCommentId(mockMember2.getId(), mockComment.getId())).willReturn(Optional.empty());
        given(voteRepository.save(any())).willReturn(commentVote);

        // when
        boolean result = voteService.voteComment(mockMember2, mockComment.getId());

        // then
        assertThat(result).isTrue();
        assertThat(mockComment.getVoteCount()).isEqualTo(1);
        verify(voteRepository, times(1)).save(any(Vote.class));

    }

    @Test
    void votePost_cancel()
    {
        // given
        mockPost.setVoteCount(1);
        given(postRepository.findByIdWithOptimisticLock(any())).willReturn(Optional.of(mockPost));
        given(voteRepository.findByMemberIdAndPostId(mockMember2.getId(), mockPost.getId())).willReturn(Optional.of(postVote));

        // when
        boolean result = voteService.votePost(mockMember2, mockPost.getId());

        // then
        assertThat(result).isFalse();
        assertThat(mockPost.getVoteCount()).isEqualTo(0);
        verify(voteRepository, never()).save(any(Vote.class));
    }


    @Test
    void voteComment_cancel()
    {
        // given
        mockComment.setVoteCount(1);
        given(commentRepository.findByIdWithOptimisticLock(any())).willReturn(Optional.of(mockComment));
        given(voteRepository.findByMemberIdAndCommentId(mockMember2.getId(), mockComment.getId())).willReturn(Optional.of(commentVote));

        // when
        boolean result = voteService.voteComment(mockMember2, mockComment.getId());

        // then
        assertThat(result).isFalse();
        assertThat(mockComment.getVoteCount()).isEqualTo(0);
        verify(voteRepository, never()).save(any(Vote.class));

    }


    @Test
    void deleteVoteByPostId()
    {
        // given
        Vote vote2 = newMockPostVote(2L, mockMember, mockPost);
        List<Vote> voteList = List.of(postVote, vote2);

        given(voteRepository.findAllByPostId(mockPost.getId())).willReturn(voteList);

        // when
        voteService.deleteAllVoteInThePost(mockPost.getId());

        // then
    }

}