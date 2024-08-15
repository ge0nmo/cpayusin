package com.cpayusin.member.controller.response;

import com.cpayusin.member.domain.MemberDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
public record MemberCreateResponse(Long id, String nickname, String email, String role)
{

    public static MemberCreateResponse from(MemberDomain memberDomain)
    {
        return MemberCreateResponse.builder()
                .id(memberDomain.getId())
                .email(memberDomain.getEmail())
                .nickname(memberDomain.getNickname())
                .role(memberDomain.getRole())
                .build();
    }
}
