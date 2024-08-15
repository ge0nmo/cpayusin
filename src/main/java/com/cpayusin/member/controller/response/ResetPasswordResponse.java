package com.cpayusin.member.controller.response;

import com.cpayusin.member.domain.MemberDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
public record ResetPasswordResponse(Long id, String nickname, String email, String role)
{
    public static ResetPasswordResponse from(MemberDomain memberDomain)
    {
        return ResetPasswordResponse.builder()
                .id(memberDomain.getId())
                .nickname(memberDomain.getNickname())
                .email(memberDomain.getEmail())
                .role(memberDomain.getRole())
                .build();
    }
}
