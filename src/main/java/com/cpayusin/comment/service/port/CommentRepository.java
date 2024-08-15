package com.cpayusin.comment.service.port;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.infrastructure.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository
{
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    Optional<Comment> findByIdWithOptimisticLock(@Param("commentId") Long commentId);

    List<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType);

    Page<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType, Pageable pageable);

    List<Comment> findChildCommentsByPostId(@Param("postId") Long postId);

    Page<CommentResponseForProfile> findCommentsForProfile(@Param("memberId") Long memberId, Pageable pageable);

    Optional<Comment> findById(Long commentId);

    Comment save(Comment comment);

    void deleteById(Long commentId);

    void deleteAllInBatch(List<Comment> commentEntities);

    boolean existsById(Long commentId);
}
