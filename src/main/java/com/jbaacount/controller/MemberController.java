package com.jbaacount.controller;

import com.jbaacount.global.dto.SliceDto;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.member.EmailRequest;
import com.jbaacount.payload.request.member.MemberUpdateRequest;
import com.jbaacount.payload.request.member.NicknameRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.member.*;
import com.jbaacount.service.MemberService;
import com.jbaacount.validator.MemberValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController
{
    private final MemberService memberService;
    private final MemberValidator memberValidator;

    @PatchMapping("/update")
    public ResponseEntity<GlobalResponse<MemberUpdateResponse>> updateMember(@RequestPart(value = "data", required = false) @Valid MemberUpdateRequest patchDto,
                                                                             @RequestPart(value = "image", required = false)MultipartFile multipartFile,
                                                                             @AuthenticationPrincipal MemberDetails currentUser)
    {
        var data = memberService.updateMember(patchDto, multipartFile, currentUser.getMember());

        log.info("===updateMember===");
        log.info("user updated successfully");
        return ResponseEntity.ok(new GlobalResponse<>(data));
    }


    @GetMapping("/profile")
    public ResponseEntity<GlobalResponse<MemberDetailResponse>> getMemberOwnProfile(@AuthenticationPrincipal MemberDetails member)
    {
        var data = memberService.getMemberDetailResponse(member.getMember().getId());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/get/{member-id}")
    public ResponseEntity<GlobalResponse<MemberSingleResponse>> getSingleMember(@PathVariable("member-id") Long memberId)
    {
        MemberSingleResponse data = memberService.getMemberSingleResponse(memberId);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }


    @GetMapping("/multi-info")
    public ResponseEntity getMemberList(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(required = false) Long member,
                                        @PageableDefault(size = 8)Pageable pageable)

    {
        log.info("===getAllMembers===");
        SliceDto<MemberMultiResponse> response = memberService.getAllMembers(keyword, member, pageable);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/score")
    public ResponseEntity<GlobalResponse<List<MemberScoreResponse>>> get3MembersByScore()
    {
        var data = memberService.findTop3MembersByScore();

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<GlobalResponse<String>> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails)
    {
        boolean result = memberService.deleteById(memberDetails.getMember());

        if(result)
            return ResponseEntity.ok(new GlobalResponse<>("삭제되었습니다."));

        else
            return ResponseEntity.ok(new GlobalResponse<>("삭제에 실패했습니다."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<GlobalResponse<String>> checkExistEmail(@RequestBody @Valid EmailRequest request)
    {
        var data = memberValidator.checkExistEmail(request.getEmail());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PostMapping("/verify-nickname")
    public ResponseEntity<GlobalResponse<String>> checkExistNickname(@RequestBody @Valid NicknameRequest request)
    {
        var data = memberValidator.checkExistNickname(request.getNickname());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }
}
