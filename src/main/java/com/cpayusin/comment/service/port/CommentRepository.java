package com.cpayusin.comment.service.port;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository
{
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findByIdWithOptimisticLock(Long commentId);

    Page<Comment> findParentCommentsByPostId(Long postId, String commentType, Pageable pageable);

    List<Comment> findChildCommentsByPostId(Long postId);

    Page<CommentResponseForProfile> findCommentsForProfile(Long memberId, Pageable pageable);

    Optional<Comment> findById(Long commentId);

    Comment save(Comment comment);

    void deleteById(Long commentId);

    void deleteAllInBatch(List<Comment> commentEntities);

    boolean existsById(Long commentId);

    List<Long> findAllByPostId(long postId);
}
