package com.cpayusin.vote.service.port;

import com.cpayusin.vote.domain.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteRepository
{
    Optional<Vote> findByMemberIdAndPostId(Long memberId, Long postId);

    List<Vote> findAllByPostId(Long postId);

    boolean existsVoteByMemberIdAndPostId(Long memberId, Long postId);

    Optional<Vote> findById(Long id);

    Vote save(Vote vote);

    void deleteById(Long id);

    void deleteAllByPostId(long postId);
}
