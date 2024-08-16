package com.cpayusin.vote.repository;

import com.cpayusin.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteJpaRepository extends JpaRepository<Vote, Long>
{
    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.post.id = :postId ")
    Optional<Vote> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.comment.id = :commentId ")
    Optional<Vote> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId")
    List<Vote> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT v FROM Vote v WHERE v.comment.id = :commentId")
    List<Vote> findAllByCommentId(@Param("commentId") Long commentId);

    boolean existsVoteByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    boolean existsVoteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

}
