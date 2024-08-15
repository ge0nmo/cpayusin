package com.cpayusin.member.controller.response;

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
