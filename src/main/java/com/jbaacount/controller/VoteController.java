package com.jbaacount.controller;

import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
@RestController
public class VoteController
{
    private final VoteService voteService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> votePost(@AuthenticationPrincipal MemberDetails currentMember,
                                      @PathVariable("postId") Long postId)
    {
        boolean response = voteService.votePost(currentMember.getMember(), postId);
        if(response)
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 성공"));

        else
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 취소"));
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> voteComment(@AuthenticationPrincipal MemberDetails currentMember,
                                         @PathVariable("commentId") Long commentId)
    {
        boolean response = voteService.voteComment(currentMember.getMember(), commentId);

        if(response)
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 성공"));

        else
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 취소"));
    }
}
