package com.cpayusin.common.service;

import com.cpayusin.common.exception.AuthenticationException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.member.domain.type.Role;
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

    public void checkPermission(Long memberId, MemberDomain memberDomain)
    {
        if (Role.ADMIN.getValue().equals(memberDomain.getRole()))
            return;

        isTheSameUser(memberId, memberDomain.getId());
    }

    public void isTheSameUser(Long memberId, Long loggedInMemberId)
    {
        if(memberId.longValue() != loggedInMemberId.longValue())
            throw new AuthenticationException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isAdmin(MemberDomain memberDomain)
    {
        if(!memberDomain.getRole().equals(Role.ADMIN.getValue()))
            throw new AuthenticationException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isUserAllowed(Boolean isAdminOnly, MemberDomain currentMember)
    {
        if(isAdminOnly)
            isAdmin(currentMember);
    }

    public static String generateVerificationCode()
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            sb.append(WORDS.charAt(random.nextInt(WORDS.length())));
        }
        return sb.toString();
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
