package com.cpayusin.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cpayusin.common.utils.FilenameGenerator;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.domain.FileDomain;
import com.cpayusin.file.infrastructure.File;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.service.port.FileRepository;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.post.domain.PostDomain;
import com.cpayusin.post.infrastructure.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class FileServiceImpl implements FileService
{
    private final FileRepository fileRepository;
    private final FilenameGenerator filenameGenerator;
    private final AmazonS3 amazonS3;

    private final static String bucket = "jbaccount";


    @Transactional
    @Override
    public FileDomain save(FileDomain file)
    {
        return fileRepository.save(file);
    }

    @Transactional
    @Override
    public FileDomain saveForOauth2(String picture, MemberDomain member)
    {
        FileDomain fileDomain = FileDomain.fromOAuth2SignUp(picture, member);

        return fileRepository.save(fileDomain);
    }

    @Transactional
    @Override
    public FileDomain updateForOAuth2(String picture, MemberDomain memberDomain)
    {
        Optional<FileDomain> optionalFileDomain = getFileByMemberId(memberDomain.getId());

        if(optionalFileDomain.isPresent()) {
            FileDomain fileDomain = optionalFileDomain.get();
            return fileDomain.fromOAuth2Update(picture);
        } else{
            return FileDomain.fromOAuth2SignUp(picture, memberDomain);
        }
    }


    @Transactional
    @Override
    public List<FileDomain> storeFiles(List<MultipartFile> files, PostDomain postDomain)
    {
        List<File> storedFileEntities = new ArrayList<>();

        for(MultipartFile file : files)
        {
            File storedFile = storeFileInPost(file, post);
            storedFileEntities.add(storedFile);

        }

        return storedFileEntities;
    }

    @Transactional
    @Override
    public void deleteUploadedFiles(Long postId)
    {
        List<FileDomain> fileEntities = fileRepository.findByPostId(postId);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    @Override
    public void deleteUploadedFiles(List<String> urls)
    {
        List<File> fileEntities = fileRepository.findAllByUrl(urls);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    @Override
    public void deleteProfilePhoto(Long memberId)
    {
        Optional<FileDomain> file = fileRepository.findByMemberId(memberId);

        if(file.isPresent())
        {
            log.info("file removed successfully = {}", file.get().getStoredFileName());
            fileRepository.deleteById(file.get().getId());
        }
    }

    @Transactional
    @Override
    public String storeProfileImage(MultipartFile multipartFile, MemberDomain member)
    {
        String ext = multipartFile.getContentType();
        if(!ext.contains("image"))
            throw new BusinessLogicException(ExceptionMessage.EXT_NOT_ACCEPTED);

        String uploadFileName = multipartFile.getOriginalFilename();
        String storeFileName = filenameGenerator.createStoreFileName(uploadFileName);
        String location = "profile/";

        try{
            saveUploadFile(storeFileName, multipartFile, location);
        } catch (IOException e){
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }

        File file = File.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .url(getFileUrl(storeFileName, location))
                .contentType(extractContentType(multipartFile))
                .build();

        file.addMember(member);
        return fileRepository.save(file).getUrl();
    }

    public List<String> getFileUrlByPostId(Long postId)
    {
        return fileRepository.findUrlByPostId(postId);
    }



    private Optional<FileDomain> getFileByMemberId(Long memberId)
    {
        return fileRepository.findByMemberId(memberId);
    }

    private void deleteFiles(List<FileDomain> fileEntities)
    {
        if(fileEntities != null && !fileEntities.isEmpty())
        {
            fileEntities.forEach(img -> {
                amazonS3.deleteObject(bucket, "postEntity/" + img.getStoredFileName());
                log.info("file removed successfully = {}", img.getStoredFileName());
            });
        }
    }


    private FileDomain storeFileInPost(MultipartFile multipartFile, Post post)
    {
        String uploadFileName = multipartFile.getOriginalFilename();
        String storeFileName = filenameGenerator.createStoreFileName(uploadFileName);
        String location = "postEntity/";

        try{
            saveUploadFile(storeFileName, multipartFile, location);
        } catch (IOException e){
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }

        File file = File.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .url(getFileUrl(storeFileName, location))
                .contentType(extractContentType(multipartFile))
                .build();


        File filePS = fileRepository.save(file);
        filePS.addPost(post);

        return filePS;
    }

    private void saveUploadFile(String storeFileName, MultipartFile file, String location) throws IOException
    {
        String contentType = extractContentType(file);
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, location + storeFileName, file.getInputStream(), metadata);
    }

    private String getFileUrl(String fileName, String location)
    {
        return amazonS3.getUrl(bucket,  location + fileName).toString();
    }

    private String extractContentType(MultipartFile multipartFile)
    {
        String contentType = multipartFile.getContentType();
        String ext = filenameGenerator.extractedEXT(multipartFile.getOriginalFilename());
        log.info("content type = {}", contentType);

        if(contentType == null || "application/octet-stream".equals(contentType))
        {
            switch (ext.toLowerCase())
            {
                case "jfif":
                    contentType = "image/jpeg";
                    break;
            }
        }

        return contentType;
    }


}