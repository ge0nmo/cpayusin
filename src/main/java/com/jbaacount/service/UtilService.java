package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.model.Member;
import com.jbaacount.model.type.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class UtilService
{
    private static final String WORDS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String REMOVED_EMAIL = "deleted email_";
    private static final String REMOVED_NICKNAME = "deleted user_";

    public void checkPermission(Long memberId, Member currentMember)
    {
        if (Role.ADMIN.getValue().equals(currentMember.getRole()))
            return;

        isTheSameUser(memberId, currentMember.getId());
    }

    public void isTheSameUser(Long memberId, Long loggedInMemberId)
    {
        if(memberId.longValue() != loggedInMemberId.longValue())
            throw new BusinessLogicException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isAdmin(Member currentMember)
    {
        if(!currentMember.getRole().equals(Role.ADMIN.getValue()))
            throw new BusinessLogicException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isUserAllowed(Boolean isAdminOnly, Member currentMember)
    {
        if(isAdminOnly)
            isAdmin(currentMember);
    }

    public static String generateVerificationCode()
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 8; i++)
        {
            sb.append(WORDS.charAt(random.nextInt(WORDS.length())));
        }
        String verificationCode = sb.toString();

        return verificationCode;
    }

    public static String generateRemovedEmail()
    {
        return REMOVED_EMAIL + UUID.randomUUID().toString();
    }

    public static String generateRemovedNickname()
    {
        return REMOVED_NICKNAME + UUID.randomUUID().toString();
    }
}
