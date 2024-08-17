package com.cpayusin.comment.controller;

import com.cpayusin.comment.controller.port.CommentFacade;
import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.request.CommentUpdateRequest;
import com.cpayusin.comment.controller.response.*;
import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.member.domain.Member;
import com.cpayusin.common.controller.response.GlobalResponse;
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
    private final CommentFacade commentFacade;

    @PostMapping("/comment/create")
    public ResponseEntity<GlobalResponse<CommentCreatedResponse>> saveComment(@RequestBody @Valid CommentCreateRequest request,
                                                                              @AuthenticationPrincipal MemberDetails member) throws InterruptedException
    {
        CommentCreatedResponse data = commentFacade.saveComment(request, member.getMember());

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
    public ResponseEntity<GlobalResponse<CommentMultiResponse>> getCommentsByPostId(@RequestParam("postId") Long postId,
                                                                                    @PageableDefault Pageable pageable,
                                                                                    @AuthenticationPrincipal MemberDetails currentMember)
    {
        Member member = currentMember != null ? currentMember.getMember() : null;
        var data = commentService.getCommentsByPostId(postId, member, pageable.previousOrFirst());

        return ResponseEntity.ok(data);
    }



    @GetMapping("/profile/my-comments")
    public ResponseEntity<GlobalResponse<List<CommentResponseForProfile>>> getAllCommentsForProfile(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                                                    @PageableDefault(size = 8) Pageable pageable)
    {
        Page<CommentResponseForProfile> data = commentService.getAllCommentsForProfile(memberDetails.getMember(), pageable.previousOrFirst());

        return ResponseEntity.ok(new GlobalResponse<>(data.getContent(), PageInfo.of(data)));
    }


    @DeleteMapping("/comment/delete/{comment-id}")
    public ResponseEntity<GlobalResponse<String>> deleteComment(@PathVariable("comment-id") Long commentId,
                                        @AuthenticationPrincipal MemberDetails memberDetails) throws InterruptedException
    {

        boolean result = commentFacade.deleteComment(commentId, memberDetails.getMember());

        if(result)
            return ResponseEntity.ok(new GlobalResponse<>("댓글을 삭제했습니다."));

        else
            return ResponseEntity.ok(new GlobalResponse<>("댓글 삭제에 실패했습니다."));
    }
}
