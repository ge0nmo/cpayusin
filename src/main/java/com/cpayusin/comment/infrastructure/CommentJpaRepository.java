package com.cpayusin.comment.infrastructure;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long>
{
    List<CommentEntity> findAllByPostEntityId(@Param("postId") Long postId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM CommentEntity c WHERE c.id = :commentId ")
    Optional<CommentEntity> findByIdWithOptimisticLock(@Param("commentId") Long commentId);

    @Query("SELECT c FROM CommentEntity c WHERE c.postEntity.id = :postId AND c.type = :commentType ORDER BY c.createdAt ASC")
    List<CommentEntity> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType);

    @Query("SELECT c FROM CommentEntity c WHERE c.postEntity.id = :postId AND c.type = :commentType ORDER BY c.createdAt ASC")
    Page<CommentEntity> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.postEntity.id = :postId AND c.parent IS NOT NULL ")
    List<CommentEntity> findChildCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT new com.cpayusin.comment.controller.response.CommentResponseForProfile(" +
            "p.id, " +
            "p.title, " +
            "p.boardEntity.id, " +
            "p.boardEntity.name, " +
            "c.id, " +
            "c.text, " +
            "c.voteCount, " +
            "c.createdAt) " +
            "FROM CommentEntity c " +
            "JOIN PostEntity p ON p.id = c.postEntity.id " +
            "WHERE (c.memberEntity.id = :memberId) " +
            "AND (c.isRemoved = FALSE )" +
            "ORDER BY c.createdAt DESC")
    Page<CommentResponseForProfile> findCommentsForProfile(@Param("memberId") Long memberId,
                                                           Pageable pageable);
}
