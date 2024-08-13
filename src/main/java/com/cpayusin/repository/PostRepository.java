package com.cpayusin.repository;

import com.cpayusin.model.Post;
import com.cpayusin.payload.response.post.PostResponseForProfile;
import com.cpayusin.payload.response.post.PostResponseProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository
{
    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId")
    List<Post> findAllByBoardId(@Param("boardId") Long boardId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithOptimisticLock(@Param("id") Long id);

    @Query(
            value = "SELECT " +
                    "m.id AS memberId, " +
                    "m.nickname AS memberName, " +
                    "b.id AS boardId, " +
                    "b.name AS boardName, " +
                    "p.id AS postId, " +
                    "p.title, " +
                    "p.vote_count AS voteCount, " +
                    "p.comment_count AS commentCount, " +
                    "p.created_at AS createdAt " +
                    "FROM post p " +
                    "JOIN member m ON m.id = p.member_id " +
                    "JOIN board b ON b.id = p.board_id " +
                    "WHERE b.id IN :boardIds " +
                    "ORDER BY p.created_at DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM post p " +
                    "JOIN board b ON b.id = p.board_id " +
                    "WHERE b.id IN :boardIds ",
            nativeQuery = true)
    Page<PostResponseProjection> findAllPostByBoardId(@Param("boardIds") List<Long> boardIds,
                                                      Pageable pageable);

    @Query("SELECT new com.cpayusin.payload.response.post.PostResponseForProfile(" +
            "b.id, " +
            "b.name, " +
            "p.id, " +
            "p.title, " +
            "p.createdAt) " +
            "FROM Post p " +
            "JOIN Board b ON b.id = p.board.id " +
            "WHERE p.member.id = :memberId ORDER BY p.createdAt DESC")
    Page<PostResponseForProfile> findAllByMemberIdForProfile(@Param("memberId") Long memberId, Pageable pageable);


    @Query(
            value = "SELECT p.* FROM post p " +
                    "JOIN member m ON m.id = p.member_id " +
                    "JOIN board b ON b.id = p.board_id " +
                    "WHERE b.id = :boardId " +
                    "AND (:keyword IS NULL OR MATCH(p.title, p.content) AGAINST (:keyword IN BOOLEAN MODE)) " +
                    "ORDER BY p.id DESC",
            countQuery = "SELECT COUNT(*) FROM post p " +
                    "JOIN board b ON b.id = p.board_id " +
                    "WHERE b.id = :boardId " +
                    "AND (:keyword IS NULL OR MATCH(p.title, p.content) AGAINST (:keyword IN BOOLEAN MODE))",
            nativeQuery = true
    )
    Page<Post> findAllByBoardId(@Param("boardId") Long boardId,
                                @Param("keyword") String keyword,
                                Pageable pageable);
}
