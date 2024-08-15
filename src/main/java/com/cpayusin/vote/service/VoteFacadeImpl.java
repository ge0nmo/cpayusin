package com.cpayusin.vote.service;

import com.cpayusin.common.utils.RetryUtil;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.vote.controller.port.VoteFacade;
import com.cpayusin.vote.controller.port.VoteService;
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
    public boolean votePost(MemberEntity memberEntity, Long postId) throws InterruptedException
    {
        return RetryUtil.retry(() -> voteService.votePost(memberEntity, postId),
                String.format("최대 시도 횟수를 초과했습니다. postId = %d, memberId = %d", postId, memberEntity.getId()));
    }

    @Override
    public boolean voteComment(MemberEntity memberEntity, Long commentId) throws InterruptedException
    {
        return RetryUtil.retry(() -> voteService.voteComment(memberEntity, commentId),
                String.format("최대 시도 횟수를 초과했습니다. postId = %d, memberId = %d", commentId, memberEntity.getId()));
    }

}
