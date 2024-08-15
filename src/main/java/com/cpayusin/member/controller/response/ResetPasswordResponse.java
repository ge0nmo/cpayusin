package com.cpayusin.member.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponse
{
    private Long id;

    private String nickname;

    private String email;

    private String role;
}
