package com.cpayusin.comment.service;

import com.cpayusin.comment.controller.port.CommentFacade;
import com.cpayusin.common.utils.RetryUtil;
import com.cpayusin.member.infrastructure.MemberEntity;
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
    public CommentCreatedResponse saveComment(CommentCreateRequest request, MemberEntity memberEntity) throws InterruptedException
    {
        return RetryUtil.retry(() -> commentService.saveComment(request, memberEntity),
                String.format("최대 시도 횟수를 초과했습니다. memberId = %d", memberEntity.getId()));
    }

    @Override
    public boolean deleteComment(Long commentId, MemberEntity currentMemberEntity) throws InterruptedException
    {
        return RetryUtil.retry(() -> commentService.deleteComment(commentId, currentMemberEntity),
                String.format("최대 시도 횟수를 초과했습니다. memberId = %d", currentMemberEntity.getId()));
    }
}
