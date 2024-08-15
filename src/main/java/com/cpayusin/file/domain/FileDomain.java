package com.cpayusin.file.domain;

import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.post.domain.PostDomain;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class FileDomain
{
    private Long id;
    private String uploadFileName;
    private String storedFileName;
    private String url;
    private String contentType;
    private PostDomain postDomain;
    private MemberDomain memberDomain;

    public static FileDomain fromProfileUpload(String uploadFileName,
                                               String storedFileName,
                                               String url,
                                               String contentType,
                                               MemberDomain memberDomain)
    {
        return FileDomain.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .url(url)
                .contentType(contentType)
                .memberDomain(memberDomain)
                .build();
    }

    public static FileDomain fileProfileUpload(String uploadFileName,
                                               String storedFileName,
                                               String url,
                                               String contentType,
                                               MemberDomain memberDomain)
    {
        return FileDomain.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .url(url)
                .contentType(contentType)
                .memberDomain(memberDomain)
                .build();
    }

    public static FileDomain fromOAuth2SignUp(String picture, MemberDomain memberDomain)
    {
        return FileDomain.builder()
                .uploadFileName(UUID.randomUUID().toString())
                .storedFileName(UUID.randomUUID().toString())
                .url(picture)
                .contentType(UUID.randomUUID().toString())
                .memberDomain(memberDomain)
                .build();
    }

    public FileDomain fromOAuth2Update(String picture)
    {
        return FileDomain.builder()
                .id(id)
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .url(picture)
                .contentType(contentType)
                .memberDomain(memberDomain)
                .build();
    }
}
