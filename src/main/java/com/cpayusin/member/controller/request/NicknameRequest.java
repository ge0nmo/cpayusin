package com.cpayusin.member.controller.request;

import com.cpayusin.common.validation.NicknameValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NicknameRequest
{
    @NicknameValidation
    private String nickname;
}
