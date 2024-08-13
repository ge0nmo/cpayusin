package com.cpayusin.facade.impl;

import com.cpayusin.facade.RetryUtil;
import com.cpayusin.facade.VoteFacade;
import com.cpayusin.model.Member;
import com.cpayusin.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class VoteFacadeImpl implements VoteFacade
{
    private final VoteService voteService;

    @Override
    public boolean votePost(Member member, Long postId) throws InterruptedException
    {
        return RetryUtil.retry(() -> voteService.votePost(member, postId),
                String.format("최대 시도 횟수를 초과했습니다. postId = %d, memberId = %d", postId, member.getId()));
    }

    @Override
    public boolean voteComment(Member member, Long commentId) throws InterruptedException
    {
        return RetryUtil.retry(() -> voteService.voteComment(member, commentId),
                String.format("최대 시도 횟수를 초과했습니다. postId = %d, memberId = %d", commentId, member.getId()));
    }

}
