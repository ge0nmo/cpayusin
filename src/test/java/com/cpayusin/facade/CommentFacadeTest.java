package com.cpayusin.facade;

import com.cpayusin.comment.controller.port.CommentFacade;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
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
        for(Member member : memberEntities){
            es.submit(() -> {
                try{
                    commentFacade.saveComment(request, member);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Post post = postRepository.findById(1L).orElseThrow();
        assertThat(post.getCommentCount()).isEqualTo(10);
    }

    @Test
    void saveCommentWithoutConcurrencyControl() throws InterruptedException
    {
        // given

        // when
        for(Member member : memberEntities){
            es.submit(() -> {
                try{
                    commentService.saveComment(request, member);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Post post = postRepository.findById(1L).orElseThrow();
        assertThat(post.getCommentCount()).isNotEqualTo(10);
    }

/*    @Test
    void deleteCommentWithOptimisticLock() throws InterruptedException
    {
        // given
        for(Member member : memberEntities){
            commentService.saveComment(request, member);
            System.out.println("comment saved");
        }

        Post beforePost = postRepository.findById(1L).orElseThrow();

        // when
        for(Member member : memberEntities){
            es.submit(() -> {
                try{
                    commentFacade.deleteComment(mockPost.getId(), member);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Post afterPost = postRepository.findById(1L).orElseThrow();
        int commentCount = beforePost.getCommentCount() - memberEntities.size();
        assertThat(afterPost.getCommentCount()).isEqualTo(commentCount);
    }*/

}
