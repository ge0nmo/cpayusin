package com.cpayusin.comment.controller.port;

import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.request.CommentUpdateRequest;
import com.cpayusin.comment.controller.response.*;
import com.cpayusin.comment.domain.Comment;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, Member currentMember);

    CommentUpdateResponse updateComment(CommentUpdateRequest request, Long commentId, Member currentMember);

    Comment getComment(Long commentId);

    GlobalResponse<CommentMultiResponse> getCommentsByPostId(Long postId, Pageable pageable);

    Page<CommentResponseForProfile> getAllCommentsForProfile(Member member, Pageable pageable);

    CommentSingleResponse getCommentSingleResponse(Long commentId);

    boolean deleteComment(Long commentId, Member currentMember);

    void deleteAllByPostId(Long postId);
}
