package com.jbaacount.payload.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateResponse
{
    private Long id;

    private String nickname;

    private String email;

    private String role;

    private int score;
}
