package com.jbaacount.payload.response.member;

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

    private int score;
    private String role;
}
