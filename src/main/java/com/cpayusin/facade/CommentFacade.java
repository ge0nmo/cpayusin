package com.cpayusin.facade;

import com.cpayusin.model.Member;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.payload.response.comment.CommentCreatedResponse;

public interface CommentFacade
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, Member member) throws InterruptedException;

    boolean deleteComment(Long commentId, Member currentMember) throws InterruptedException;
}
