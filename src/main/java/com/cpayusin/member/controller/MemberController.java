package com.cpayusin.member.controller;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.controller.request.EmailRequest;
import com.cpayusin.member.controller.request.NicknameRequest;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.service.MemberValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<GlobalResponse<MemberUpdateResponse>> updateMember(@RequestBody @Valid MemberUpdateRequest patchDto,
                                                                             @AuthenticationPrincipal MemberDetails currentUser)
    {
        var data = memberService.updateMember(patchDto, currentUser.getMember());

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
    public ResponseEntity<SliceDto<MemberMultiResponse>> getMemberList(@RequestParam(value = "keyword", required = false) String keyword,
                                                                       @RequestParam(required = false) Long member,
                                                                       @PageableDefault(size = 8)Pageable pageable)
    {
        log.info("===getAllMembers===");
        SliceDto<MemberMultiResponse> response = memberService.getAllMembers(keyword, member, pageable);

        return ResponseEntity.ok(response);
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
