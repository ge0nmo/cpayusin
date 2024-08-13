package com.cpayusin.global.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException
{
    private final ExceptionMessage exceptionMessage;


    public BusinessLogicException(ExceptionMessage exceptionMessage)
    {
        super(exceptionMessage.getMessage());
        this.exceptionMessage = exceptionMessage;
    }
}
