package com.cpayusin.facade.impl;

import com.cpayusin.facade.CommentFacade;
import com.cpayusin.facade.RetryUtil;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.payload.response.comment.CommentCreatedResponse;
import com.cpayusin.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class CommentFacadeImpl implements CommentFacade
{
    private final CommentService commentService;

    @Override
    public CommentCreatedResponse saveComment(CommentCreateRequest request, Member member) throws InterruptedException
    {
        return RetryUtil.retry(() -> commentService.saveComment(request, member),
                String.format("최대 시도 횟수를 초과했습니다. memberId = %d", member.getId()));
    }

    @Override
    public boolean deleteComment(Long commentId, Member currentMember) throws InterruptedException
    {
        return RetryUtil.retry(() -> commentService.deleteComment(commentId, currentMember),
                String.format("최대 시도 횟수를 초과했습니다. memberId = %d", currentMember.getId()));
    }
}
