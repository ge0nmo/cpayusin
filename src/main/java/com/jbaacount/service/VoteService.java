package com.jbaacount.service;

import com.jbaacount.model.Member;

public interface VoteService
{
    boolean votePost(Member currentMember, Long postId);

    boolean voteComment(Member currentMember, Long commentId);

    void deleteAllVoteInThePost(Long postId);

    void deleteAllVoteInTheComment(Long commentId);

    boolean checkIfMemberVotedPost(Long memberId, Long postId);

    boolean checkIfMemberVotedComment(Long memberId, Long commentId);
}
