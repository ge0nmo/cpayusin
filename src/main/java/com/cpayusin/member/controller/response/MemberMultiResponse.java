package com.cpayusin.member.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberMultiResponse
{
    private Long id;

    private String nickname;

    private String email;

    @JsonProperty("profileImage")
    private String url;

    private String role;
}
