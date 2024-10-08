package com.cpayusin.member.controller.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSingleResponse
{
    private Long id;

    private String nickname;

    private String profileImage;

    private String role;
}
