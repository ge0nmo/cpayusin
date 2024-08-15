package com.cpayusin.vote.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteJpaRepository extends JpaRepository<VoteEntity, Long>
{
    @Query("SELECT v FROM VoteEntity v WHERE v.memberEntity.id = :memberId AND v.postEntity.id = :postId ")
    Optional<VoteEntity> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT v FROM VoteEntity v WHERE v.memberEntity.id = :memberId AND v.commentEntity.id = :commentId ")
    Optional<VoteEntity> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    @Query("SELECT v FROM VoteEntity v WHERE v.postEntity.id = :postId")
    List<VoteEntity> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT v FROM VoteEntity v WHERE v.commentEntity.id = :commentId")
    List<VoteEntity> findAllByCommentId(@Param("commentId") Long commentId);

    boolean existsVoteByMemberEntityIdAndCommentEntityId(@Param("memberEntityId") Long memberId, @Param("commentEntityId") Long commentId);

    boolean existsVoteByMemberEntityIdAndPostEntityId(@Param("memberEntityId") Long memberId, @Param("postEntityId") Long postId);

}
