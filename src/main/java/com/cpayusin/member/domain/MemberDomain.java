package com.cpayusin.member.domain;

import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.domain.type.Platform;
import com.cpayusin.member.domain.type.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import static com.cpayusin.common.service.UtilService.generateRemovedEmail;
import static com.cpayusin.common.service.UtilService.generateRemovedNickname;

@Builder
@Getter
public class MemberDomain
{
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private String url;
    private String role;
    private boolean isRemoved;
    private Platform platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberDomain from(MemberRegisterRequest request, PasswordEncoder passwordEncoder)
    {
        return MemberDomain.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER.getValue())
                .platform(Platform.HOME)
                .isRemoved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public MemberDomain update(MemberUpdateRequest request, String url, PasswordEncoder passwordEncoder)
    {
        return MemberDomain.builder()
                .id(id)
                .email(email)
                .nickname(StringUtils.hasLength(request.getNickname())
                        ? request.getNickname() : nickname)
                .password(StringUtils.hasLength(request.getPassword())
                        ? passwordEncoder.encode(request.getPassword()) : password)
                .role(role)
                .isRemoved(isRemoved)
                .url(StringUtils.hasLength(url)
                        ? url : null)
                .platform(platform)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public MemberDomain resetPassword(String password, PasswordEncoder passwordEncoder)
    {
        return MemberDomain.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(role)
                .platform(platform)
                .isRemoved(false)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();

    }

    public MemberDomain removeMemberLogic()
    {
        return MemberDomain.builder()
                .id(id)
                .email(generateRemovedEmail())
                .nickname(generateRemovedNickname())
                .password(password)
                .isRemoved(true)
                .role(role)
                .platform(platform)
                .updatedAt(LocalDateTime.now())
                .createdAt(createdAt)
                .build();
    }
}
