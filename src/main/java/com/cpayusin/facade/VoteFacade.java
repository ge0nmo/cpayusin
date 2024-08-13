package com.cpayusin.facade;

import com.cpayusin.model.Member;

public interface VoteFacade
{
    boolean votePost(Member member, Long postId) throws InterruptedException;

    boolean voteComment(Member member, Long commentId) throws InterruptedException;
}
