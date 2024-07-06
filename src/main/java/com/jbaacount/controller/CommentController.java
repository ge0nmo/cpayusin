package com.jbaacount.controller;

import com.jbaacount.global.dto.PageInfo;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.comment.CommentCreateRequest;
import com.jbaacount.payload.request.comment.CommentUpdateRequest;
import com.jbaacount.payload.response.*;
import com.jbaacount.payload.response.comment.*;
import com.jbaacount.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentController
{
    private final CommentService commentService;

    @PostMapping("/comment/create")
    public ResponseEntity<GlobalResponse<CommentCreatedResponse>> saveComment(@RequestBody @Valid CommentCreateRequest request,
                                                                              @AuthenticationPrincipal MemberDetails member)
    {
        var data = commentService.saveComment(request, member.getMember());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PatchMapping("/comment/update/{commentId}")
    public ResponseEntity<GlobalResponse<CommentUpdateResponse>> updateComment(@RequestBody @Valid CommentUpdateRequest request,
                                                                               @PathVariable("commentId") Long commentId,
                                                                               @AuthenticationPrincipal MemberDetails currentMember)
    {
        var data = commentService.updateComment(request, commentId, currentMember.getMember());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/comment/{comment-id}")
    public ResponseEntity<GlobalResponse<CommentSingleResponse>> getComment(@PathVariable("comment-id") Long commentId,
                                                                            @AuthenticationPrincipal MemberDetails currentMember)
    {
        Member member = currentMember != null ? currentMember.getMember() : null;
        var data = commentService.getCommentSingleResponse(commentId, member);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/comment")
    public ResponseEntity<GlobalResponse<List<CommentMultiResponse>>> getAllComments(@RequestParam("postId") Long postId,
                                                                                     @AuthenticationPrincipal MemberDetails currentMember)
    {
        Member member = currentMember != null ? currentMember.getMember() : null;
        var data = commentService.getAllCommentByPostId(postId, member);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/profile/my-comments")
    public ResponseEntity<GlobalResponse<List<CommentResponseForProfile>>> getAllCommentsForProfile(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                                                    @PageableDefault(size = 8) Pageable pageable)
    {
        Page<CommentResponseForProfile> data = commentService.getAllCommentsForProfile(memberDetails.getMember(), pageable.previousOrFirst());

        return ResponseEntity.ok(new GlobalResponse<>(data.getContent(), PageInfo.of(data)));
    }


    @DeleteMapping("/comment/delete/{comment-id}")
    public ResponseEntity deleteComment(@PathVariable("comment-id") Long commentId,
                                        @AuthenticationPrincipal Member currentMember)
    {
        boolean result = commentService.deleteComment(commentId, currentMember);

        if(result)
            return ResponseEntity.ok(new GlobalResponse("댓글을 삭제했습니다."));

        else
            return ResponseEntity.ok(new GlobalResponse("댓글 삭제에 실패했습니다."));
    }
}
