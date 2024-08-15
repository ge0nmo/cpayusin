package com.cpayusin.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException
{
    private final ExceptionMessage exceptionMessage;

    public AuthenticationException(ExceptionMessage exceptionMessage)
    {
        super(exceptionMessage.getMessage());
        this.exceptionMessage = exceptionMessage;
    }
}
