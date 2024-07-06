package com.jbaacount.payload.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberScoreResponse
{
    private Long id;
    private String nickname;
    private Integer score;
}
