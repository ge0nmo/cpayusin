package com.cpayusin.comment.repository;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.domain.Comment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<Comment, Long>
{
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId ")
    Optional<Comment> findByIdWithOptimisticLock(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.type = :commentType ORDER BY c.createdAt ASC")
    Page<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NOT NULL ")
    List<Comment> findChildCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT new com.cpayusin.comment.controller.response.CommentResponseForProfile(" +
            "p.id, " +
            "p.title, " +
            "p.board.id, " +
            "p.board.name, " +
            "c.id, " +
            "c.text, " +
            "c.voteCount, " +
            "c.createdAt) " +
            "FROM Comment c " +
            "JOIN Post p ON p.id = c.post.id " +
            "WHERE (c.member.id = :memberId) " +
            "AND (c.isRemoved = FALSE )" +
            "ORDER BY c.createdAt DESC")
    Page<CommentResponseForProfile> findCommentsForProfile(@Param("memberId") Long memberId,
                                                           Pageable pageable);
}
