package com.jbaacount.global.validation;

import com.jbaacount.validator.MemberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NicknameValidator.class)
@Documented
public @interface NicknameValidation
{
    String message() default "이미 사용중인 닉네임입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Slf4j
@RequiredArgsConstructor
class NicknameValidator implements ConstraintValidator<NicknameValidation, String>
{
    private final MemberValidator memberValidator;

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext)
    {
        return !memberValidator.verifyExistNickname(nickname);
    }
}