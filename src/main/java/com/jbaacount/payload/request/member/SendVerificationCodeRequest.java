package com.jbaacount.payload.request.member;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeRequest
{
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
}
