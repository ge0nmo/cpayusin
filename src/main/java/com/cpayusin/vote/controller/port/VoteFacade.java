package com.cpayusin.vote.controller.port;

import com.cpayusin.member.domain.Member;

public interface VoteFacade
{
    boolean votePost(Member member, Long postId) throws InterruptedException;

    boolean voteComment(Member member, Long commentId) throws InterruptedException;
}
