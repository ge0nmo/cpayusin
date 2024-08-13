package com.cpayusin.service;

import com.cpayusin.model.Comment;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.payload.request.comment.CommentUpdateRequest;
import com.cpayusin.payload.response.GlobalResponse;
import com.cpayusin.payload.response.comment.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, Member currentMember);

    CommentUpdateResponse updateComment(CommentUpdateRequest request, Long commentId, Member currentMember);

    Comment getComment(Long commentId);

    GlobalResponse<CommentMultiResponse> getCommentsByPostId(Long postId, Member member, Pageable pageable);

    Page<CommentResponseForProfile> getAllCommentsForProfile(Member member, Pageable pageable);

    CommentSingleResponse getCommentSingleResponse(Long commentId, Member member);

    boolean deleteComment(Long commentId, Member currentMember);

    void deleteAllByPostId(Long postId);
}
