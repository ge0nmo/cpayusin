package com.cpayusin.vote.service.port;

import com.cpayusin.vote.infrastructure.VoteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository
{
    Optional<VoteEntity> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    Optional<VoteEntity> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    List<VoteEntity> findAllByPostId(@Param("postId") Long postId);

    List<VoteEntity> findAllByCommentId(@Param("commentId") Long commentId);

    boolean existsVoteByMemberEntityIdAndCommentEntityId(@Param("memberEntityId") Long memberId, @Param("commentEntityId") Long commentId);

    boolean existsVoteByMemberEntityIdAndPostEntityId(@Param("memberEntityId") Long memberId, @Param("postEntityId") Long postId);

    Optional<VoteEntity> findById(Long id);

    VoteEntity save(VoteEntity vote);

    void deleteById(Long id);

    void deleteAllInBatch(List<VoteEntity> votes);
}
