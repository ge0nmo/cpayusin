package com.cpayusin.vote.controller.port;

import com.cpayusin.member.infrastructure.MemberEntity;

public interface VoteService
{
    boolean votePost(MemberEntity currentMemberEntity, Long postId);

    boolean voteComment(MemberEntity currentMemberEntity, Long commentId);

    void deleteAllVoteInThePost(Long postId);

    void deleteAllVoteInTheComment(Long commentId);

    boolean checkIfMemberVotedPost(Long memberId, Long postId);

    boolean checkIfMemberVotedComment(Long memberId, Long commentId);
}
