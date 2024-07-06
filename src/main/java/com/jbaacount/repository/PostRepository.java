package com.jbaacount.repository;

import com.jbaacount.model.Post;
import com.jbaacount.payload.response.post.PostMultiResponse;
import com.jbaacount.payload.response.post.PostResponseForProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>
{
    @Query(value = "select * from post p where p.title like %:keyword%", nativeQuery = true)
    Optional<Post> findByTitle(@Param("keyword") String keyword);


    @Query("SELECT p FROM Post p " +
            "WHERE p.board.id = :boardId " +
            "AND (:keyword IS NOT NULL OR p.board.name LIKE %:keyword%) " +
            "ORDER BY p.board.createdAt DESC")
    Page<Post> findAllByBoardId(@Param("boardId") Long boardId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId")
    List<Post> findAllByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId ")
    List<Post> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new com.jbaacount.payload.response.post.PostMultiResponse(" +
            "p.member.id, " +
            "p.member.nickname, " +
            "p.board.id, " +
            "p.board.name, " +
            "p.id, " +
            "p.title, " +
            "p.content, " +
            "p.voteCount, " +
            "CAST((SELECT COUNT(c) FROM Comment c WHERE c.post = p) AS integer )," +
            "p.createdAt) " +
            "FROM Post p " +
            "JOIN Member m ON m.id = p.member.id " +
            "JOIN Board b ON b.id = p.board.id " +
            "WHERE p.board.id IN :boardIds " +
            "AND (:keyword IS NULL OR p.content LIKE %:keyword% OR p.title LIKE %:keyword%) ORDER BY p.id DESC")
    Page<PostMultiResponse> findAllPostByBoardId(@Param("boardIds") List<Long> boardIds, @Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT new com.jbaacount.payload.response.post.PostResponseForProfile(p.id, p.title, p.createdAt) FROM Post p WHERE p.member.id = :memberId ORDER BY p.id DESC")
    Page<PostResponseForProfile> findAllByMemberIdForProfile(@Param("memberId") Long memberId, Pageable pageable);


}
