package com.cpayusin.member.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateResponse
{
    private Long id;

    private String nickname;

    private String email;
    private String url;

    private String role;
}
