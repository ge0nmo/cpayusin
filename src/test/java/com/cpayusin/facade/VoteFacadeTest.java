package com.cpayusin.facade;

import com.cpayusin.comment.domain.Comment;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import com.cpayusin.vote.controller.port.VoteFacade;
import com.cpayusin.vote.controller.port.VoteService;
import com.cpayusin.setup.FacadeSetUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class VoteFacadeTest extends FacadeSetUp
{
    @Autowired
    VoteFacade voteFacade;

    @Autowired
    VoteService voteService;

    @Test
    void votePostWithOptimisticLock() throws InterruptedException
    {
        // given

        // when
        for (Member member : memberEntities) {
            es.submit(() -> {
                try {
                    voteFacade.votePost(member, mockPost.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Post post = postRepository.findById(mockPost.getId()).orElseThrow();
        assertThat(post.getVoteCount()).isEqualTo(10);
    }

    @Test
    void votePostWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for (Member member : memberEntities) {
            es.submit(() -> {
                try {
                    voteService.votePost(member, mockPost.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Post post = postRepository.findById(mockPost.getId()).orElseThrow();
        assertThat(post.getVoteCount()).isNotEqualTo(10);
    }



    @Test
    void voteCommentWithOptimisticLock() throws InterruptedException
    {
        // given
        for(Member member : memberEntities){

        }

        // when
        for (Member member : memberEntities) {
            es.submit(() -> {
                try {
                    voteFacade.voteComment(member, mockComment.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Comment comment = commentRepository.findById(mockComment.getId()).orElseThrow();
        assertThat(comment.getVoteCount()).isEqualTo(10);
    }

    @Test
    void voteCommentWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for (Member member : memberEntities) {
            es.submit(() -> {
                try {
                    voteService.voteComment(member, mockComment.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Comment comment = commentRepository.findById(mockComment.getId()).orElseThrow();
        assertThat(comment.getVoteCount()).isNotEqualTo(10);
    }
}