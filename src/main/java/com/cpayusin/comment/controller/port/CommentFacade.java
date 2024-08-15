package com.cpayusin.comment.controller.port;

import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.response.CommentCreatedResponse;

public interface CommentFacade
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, MemberEntity memberEntity) throws InterruptedException;

    boolean deleteComment(Long commentId, MemberEntity currentMemberEntity) throws InterruptedException;
}
