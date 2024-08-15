package com.cpayusin.file.controller.port;

import com.cpayusin.file.domain.FileDomain;
import com.cpayusin.file.infrastructure.File;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.post.infrastructure.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    FileDomain save(FileDomain file);

    FileDomain saveForOauth2(String picture, MemberDomain member);

    FileDomain updateForOAuth2(String picture, MemberDomain member);

    List<FileDomain> storeFiles(List<MultipartFile> files, Post post);

    void deleteUploadedFiles(Long postId);

    void deleteUploadedFiles(List<String> urls);

    void deleteProfilePhoto(Long memberId);

    String storeProfileImage(MultipartFile multipartFile, MemberDomain member);

    List<String> getFileUrlByPostId(Long postId);
}
