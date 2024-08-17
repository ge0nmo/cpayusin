package com.cpayusin.vote.controller.port;

import com.cpayusin.member.domain.Member;

public interface VoteService
{
    boolean votePost(Member currentMember, Long postId);

    void deleteAllVoteInThePost(Long postId);

    boolean checkIfMemberVotedPost(Long memberId, Long postId);

    void deleteAllByPostId(Long postId);
}
