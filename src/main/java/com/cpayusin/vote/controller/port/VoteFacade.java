package com.cpayusin.vote.controller.port;

import com.cpayusin.member.infrastructure.MemberEntity;

public interface VoteFacade
{
    boolean votePost(MemberEntity memberEntity, Long postId) throws InterruptedException;

    boolean voteComment(MemberEntity memberEntity, Long commentId) throws InterruptedException;
}
