package com.cpayusin.facade;

import com.cpayusin.comment.controller.port.CommentFacade;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.setup.FacadeSetUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CommentFacadeTest extends FacadeSetUp
{
    @Autowired
    CommentFacade commentFacade;

    @Autowired
    CommentService commentService;


    @Test
    void saveCommentWithOptimisticLock() throws InterruptedException
    {
        // given

        // when
        for(MemberEntity memberEntity : memberEntities){
            es.submit(() -> {
                try{
                    commentFacade.saveComment(request, memberEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        PostEntity post = postRepository.findById(1L).orElseThrow();
        assertThat(post.getCommentCount()).isEqualTo(10);
    }

    @Test
    void saveCommentWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for(MemberEntity memberEntity : memberEntities){
            es.submit(() -> {
                try{
                    commentService.saveComment(request, memberEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        PostEntity post = postRepository.findById(1L).orElseThrow();
        assertThat(post.getCommentCount()).isNotEqualTo(10);
    }

/*    @Test
    void deleteCommentWithOptimisticLock() throws InterruptedException
    {
        // given
        for(MemberEntity memberEntity : memberEntities){
            commentService.saveComment(request, memberEntity);
            System.out.println("comment saved");
        }

        PostEntity beforePost = postRepository.findById(1L).orElseThrow();

        // when
        for(MemberEntity memberEntity : memberEntities){
            es.submit(() -> {
                try{
                    commentFacade.deleteComment(mockPost.getId(), memberEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        PostEntity afterPost = postRepository.findById(1L).orElseThrow();
        int commentCount = beforePost.getCommentCount() - memberEntities.size();
        assertThat(afterPost.getCommentCount()).isEqualTo(commentCount);
    }*/

}
