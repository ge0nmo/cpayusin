package com.jbaacount.repository;

import com.jbaacount.model.Comment;
import com.jbaacount.payload.response.comment.CommentResponseForProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>
{
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.type = :commentType ORDER BY c.createdAt ASC")
    List<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.type = :commentType ORDER BY c.createdAt ASC")
    Page<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("commentType") String commentType, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NOT NULL ")
    List<Comment> findChildCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT new com.jbaacount.payload.response.comment.CommentResponseForProfile(" +
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
