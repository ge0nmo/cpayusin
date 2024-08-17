package com.cpayusin.vote.repository;

import com.cpayusin.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteJpaRepository extends JpaRepository<Vote, Long>
{
    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.post.id = :postId ")
    Optional<Vote> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId")
    List<Vote> findAllByPostId(@Param("postId") Long postId);

    boolean existsVoteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.post.id = :postId ")
    void deleteByPostId(@Param("postId") long postId);

}
