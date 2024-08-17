package com.cpayusin.common.utils;

import com.cpayusin.common.exception.AuthenticationException;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RetryUtil
{
    private static final int MAX_RETRY = 10;
    private static final long SLEEP_TIME = 100;

    public static<T> T retry(Supplier<T> operation, String errorMessage) throws InterruptedException
    {
        int retry = 0;
        while(true){
            try{
                return operation.get();
            } catch (AuthenticationException e){
                throw new AuthenticationException(ExceptionMessage.MEMBER_UNAUTHORIZED);
            } catch (BusinessLogicException e){
                throw new BusinessLogicException(ExceptionMessage.OBJECT_NOT_FOUND);
            } catch (Exception ex){
                ++retry;
                if(retry >= MAX_RETRY){
                    log.error(errorMessage);
                    throw new BusinessLogicException(ExceptionMessage.MAX_RETRY_COUNT);
                }

                Thread.sleep(SLEEP_TIME);
            }
        }
    }
}
