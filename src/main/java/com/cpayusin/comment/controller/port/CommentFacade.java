package com.cpayusin.comment.controller.port;

import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.response.CommentCreatedResponse;

public interface CommentFacade
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, Member member) throws InterruptedException;

    boolean deleteComment(Long commentId, Member currentMember) throws InterruptedException;
}
