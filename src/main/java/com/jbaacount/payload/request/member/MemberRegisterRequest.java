package com.jbaacount.payload.request.member;

import com.jbaacount.global.validation.CustomEmailValidation;
import com.jbaacount.global.validation.NicknameValidation;
import com.jbaacount.global.validation.notspace.NotSpace;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class MemberRegisterRequest
{
    @Length(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = "^[A-z가-힣0-9 ]{2,10}", message = "닉네임에는 특수문자 및 공백이 올 수 없습니다.")
    @NicknameValidation
    @NotSpace
    private String nickname;

    @Email(message = "유효하지 않은 이메일 형식입니다.")
    @CustomEmailValidation
    private String email;

    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;
}
