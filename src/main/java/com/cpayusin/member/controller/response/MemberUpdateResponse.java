package com.cpayusin.member.controller.response;

import com.cpayusin.member.domain.MemberDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
public record MemberUpdateResponse(Long id, String nickname, String email, String url, String role)
{
    public static MemberUpdateResponse from(MemberDomain memberDomain)
    {
        return MemberUpdateResponse.builder()
                .id(memberDomain.getId())
                .nickname(memberDomain.getNickname())
                .email(memberDomain.getEmail())
                .url(memberDomain.getUrl())
                .role(memberDomain.getRole())
                .build();
    }
}
