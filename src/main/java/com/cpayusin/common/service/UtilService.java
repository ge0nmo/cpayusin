package com.cpayusin.common.service;

import com.cpayusin.common.exception.AuthenticationException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.infrastructure.MemberEntity;
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

    public void checkPermission(Long memberId, MemberEntity currentMemberEntity)
    {
        if (Role.ADMIN.getValue().equals(currentMemberEntity.getRole()))
            return;

        isTheSameUser(memberId, currentMemberEntity.getId());
    }

    public void isTheSameUser(Long memberId, Long loggedInMemberId)
    {
        if(memberId.longValue() != loggedInMemberId.longValue())
            throw new AuthenticationException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isAdmin(MemberEntity currentMemberEntity)
    {
        if(!currentMemberEntity.getRole().equals(Role.ADMIN.getValue()))
            throw new AuthenticationException(ExceptionMessage.MEMBER_UNAUTHORIZED);
    }

    public void isUserAllowed(Boolean isAdminOnly, MemberEntity currentMemberEntity)
    {
        if(isAdminOnly)
            isAdmin(currentMemberEntity);
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
