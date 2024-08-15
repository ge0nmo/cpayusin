package com.cpayusin.comment.controller.port;

import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.request.CommentUpdateRequest;
import com.cpayusin.comment.controller.response.*;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.member.infrastructure.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService
{
    CommentCreatedResponse saveComment(CommentCreateRequest request, MemberEntity currentMemberEntity);

    CommentUpdateResponse updateComment(CommentUpdateRequest request, Long commentId, MemberEntity currentMemberEntity);

    CommentEntity getComment(Long commentId);

    GlobalResponse<CommentMultiResponse> getCommentsByPostId(Long postId, MemberEntity memberEntity, Pageable pageable);

    Page<CommentResponseForProfile> getAllCommentsForProfile(MemberEntity memberEntity, Pageable pageable);

    CommentSingleResponse getCommentSingleResponse(Long commentId, MemberEntity memberEntity);

    boolean deleteComment(Long commentId, MemberEntity currentMemberEntity);

    void deleteAllByPostId(Long postId);
}
