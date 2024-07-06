package com.jbaacount.global.validation.notspace;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotSpaceValidator implements ConstraintValidator<NotSpace, String>
{
    @Override
    public void initialize(NotSpace constraintAnnotation)
    {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        return value == null || StringUtils.hasText(value);
    }
}
