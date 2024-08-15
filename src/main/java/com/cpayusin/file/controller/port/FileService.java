package com.cpayusin.file.controller.port;

import com.cpayusin.file.infrastructure.FileEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    FileEntity save(FileEntity file);

    FileEntity saveForOauth2(String picture, MemberEntity memberEntity);

    FileEntity updateForOAuth2(String picture, MemberEntity memberEntity);

    List<FileEntity> storeFiles(List<MultipartFile> files, PostEntity post);

    void deleteUploadedFiles(Long postId);

    void deleteUploadedFiles(List<String> urls);

    void deleteProfilePhoto(Long memberId);

    String storeProfileImage(MultipartFile multipartFile, MemberEntity memberEntity);

    List<String> getFileUrlByPostId(Long postId);
}
