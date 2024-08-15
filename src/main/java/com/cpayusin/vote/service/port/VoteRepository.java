package com.cpayusin.vote.service.port;

import com.cpayusin.vote.infrastructure.Vote;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository
{
    Optional<Vote> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    Optional<Vote> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    List<Vote> findAllByPostId(@Param("postId") Long postId);

    List<Vote> findAllByCommentId(@Param("commentId") Long commentId);

    boolean existsVoteByMemberEntityIdAndCommentEntityId(@Param("memberEntityId") Long memberId, @Param("commentEntityId") Long commentId);

    boolean existsVoteByMemberEntityIdAndPostEntityId(@Param("memberEntityId") Long memberId, @Param("postEntityId") Long postId);

    Optional<Vote> findById(Long id);

    Vote save(Vote vote);

    void deleteById(Long id);

    void deleteAllInBatch(List<Vote> votes);
}
