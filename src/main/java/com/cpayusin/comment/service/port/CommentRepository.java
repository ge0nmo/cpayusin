package com.cpayusin.comment.service.port;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.infrastructure.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository
{
    List<CommentEntity> findAllByPostEntityId(@Param("postId") Long postId);

    Optional<CommentEntity> findByIdWithOptimisticLock(@Param("commentId") Long commentId);

    List<CommentEntity> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType);

    Page<CommentEntity> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType, Pageable pageable);

    List<CommentEntity> findChildCommentsByPostId(@Param("postId") Long postId);

    Page<CommentResponseForProfile> findCommentsForProfile(@Param("memberId") Long memberId, Pageable pageable);

    Optional<CommentEntity> findById(Long commentId);

    CommentEntity save(CommentEntity comment);

    void deleteById(Long commentId);

    void deleteAllInBatch(List<CommentEntity> commentEntities);

    boolean existsById(Long commentId);
}
