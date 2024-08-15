package com.cpayusin.common.exception;

import lombok.Getter;

public class InvalidTokenException extends RuntimeException
{
    @Getter
    private ExceptionMessage exceptionMessage;

    public InvalidTokenException(ExceptionMessage exceptionMessage)
    {
        super(exceptionMessage.getMessage());
        this.exceptionMessage = exceptionMessage;
    }
}
