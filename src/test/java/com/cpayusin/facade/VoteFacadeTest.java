package com.cpayusin.facade;

import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
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
        for (MemberEntity memberEntity : memberEntities) {
            es.submit(() -> {
                try {
                    voteFacade.votePost(memberEntity, mockPost.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        PostEntity post = postRepository.findById(mockPost.getId()).orElseThrow();
        assertThat(post.getVoteCount()).isEqualTo(10);
    }

    @Test
    void votePostWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for (MemberEntity memberEntity : memberEntities) {
            es.submit(() -> {
                try {
                    voteService.votePost(memberEntity, mockPost.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        PostEntity post = postRepository.findById(mockPost.getId()).orElseThrow();
        assertThat(post.getVoteCount()).isNotEqualTo(10);
    }



    @Test
    void voteCommentWithOptimisticLock() throws InterruptedException
    {
        // given
        for(MemberEntity memberEntity : memberEntities){

        }

        // when
        for (MemberEntity memberEntity : memberEntities) {
            es.submit(() -> {
                try {
                    voteFacade.voteComment(memberEntity, mockCommentEntity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        CommentEntity commentEntity = commentRepository.findById(mockCommentEntity.getId()).orElseThrow();
        assertThat(commentEntity.getVoteCount()).isEqualTo(10);
    }

    @Test
    void voteCommentWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for (MemberEntity memberEntity : memberEntities) {
            es.submit(() -> {
                try {
                    voteService.voteComment(memberEntity, mockCommentEntity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        CommentEntity commentEntity = commentRepository.findById(mockCommentEntity.getId()).orElseThrow();
        assertThat(commentEntity.getVoteCount()).isNotEqualTo(10);
    }
}