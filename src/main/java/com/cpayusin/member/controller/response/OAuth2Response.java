package com.cpayusin.member.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OAuth2Response
{

    private String email;
    private String name;
    private String picture;
    private String role;

    private String accessToken;
    private String refreshToken;
}
