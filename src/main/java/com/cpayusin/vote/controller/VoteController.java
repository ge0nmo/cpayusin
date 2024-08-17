package com.cpayusin.vote.controller;

import com.cpayusin.vote.controller.port.VoteFacade;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.common.controller.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
@RestController
public class VoteController
{
    private final VoteFacade voteFacade;

    @PostMapping("/post/{postId}")
    public ResponseEntity<GlobalResponse<String>> votePost(@AuthenticationPrincipal MemberDetails currentMember,
                                      @PathVariable("postId") Long postId) throws InterruptedException
    {
        boolean response = voteFacade.votePost(currentMember.getMember(), postId);
        if(response)
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 성공"));

        else
            return ResponseEntity.ok(new GlobalResponse<>("좋아요 취소"));
    }
}
