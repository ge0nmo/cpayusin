package com.cpayusin.member.controller.response;

import com.cpayusin.member.domain.MemberDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
public record MemberDetailResponse(long id, String nickname, String email, @JsonProperty("profileImage") String url,
                                   @JsonFormat(pattern = "yyyy-MM-dd hh:mm") LocalDateTime createdAt, String role)
{
    public static MemberDetailResponse from(MemberDomain memberDomain)
    {
        return MemberDetailResponse.builder()
                .id(memberDomain.getId())
                .nickname(memberDomain.getNickname())
                .email(memberDomain.getEmail())
                .url(memberDomain.getUrl())
                .createdAt(memberDomain.getCreatedAt())
                .build();
    }
}
