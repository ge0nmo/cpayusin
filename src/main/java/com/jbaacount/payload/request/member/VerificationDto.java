package com.jbaacount.payload.request.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationDto
{
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "인증 코드를 입력해주세요.")
    private String verificationCode;
}
