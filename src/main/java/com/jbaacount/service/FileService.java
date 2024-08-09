package com.jbaacount.service;

import com.jbaacount.model.File;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    File save(File file);

    File saveForOauth2(String picture, Member member);

    File updateForOAuth2(String picture, Member member);

    List<File> storeFiles(List<MultipartFile> files, Post post);

    void deleteUploadedFiles(Long postId);

    void deleteUploadedFiles(List<String> urls);

    void deleteProfilePhoto(Long memberId);

    String storeProfileImage(MultipartFile multipartFile, Member member);

    List<String> getFileUrlByPostId(Long postId);
}
