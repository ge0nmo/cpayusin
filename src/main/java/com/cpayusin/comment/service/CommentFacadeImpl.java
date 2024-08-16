package com.cpayusin.comment.service;

import com.cpayusin.comment.controller.port.CommentFacade;
import com.cpayusin.common.utils.RetryUtil;
import com.cpayusin.member.domain.Member;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.response.CommentCreatedResponse;
import com.cpayusin.comment.controller.port.CommentService;
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
