package com.cpayusin.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cpayusin.common.utils.FilenameGenerator;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.infrastructure.FileEntity;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.service.port.FileRepository;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public FileEntity save(FileEntity file)
    {
        return fileRepository.save(file);
    }

    @Transactional
    public FileEntity saveForOauth2(String picture, MemberEntity memberEntity)
    {
        FileEntity file = FileEntity
                .builder()
                .uploadFileName(UUID.randomUUID().toString())
                .storeFileName(UUID.randomUUID().toString())
                .url(picture)
                .contentType(UUID.randomUUID().toString())
                .build();

        file.addMember(memberEntity);

        return fileRepository.save(file);
    }

    @Transactional
    public FileEntity updateForOAuth2(String picture, MemberEntity memberEntity)
    {
        FileEntity file = getFileByMemberId(memberEntity.getId())
                .orElseGet(() -> {
                    return saveForOauth2(picture, memberEntity);
                });

        file.setUrl(picture);

        return file;
    }


    @Transactional
    public List<FileEntity> storeFiles(List<MultipartFile> files, PostEntity post)
    {
        List<FileEntity> storedFileEntities = new ArrayList<>();

        for(MultipartFile file : files)
        {
            FileEntity storedFile = storeFileInPost(file, post);
            storedFileEntities.add(storedFile);

        }

        return storedFileEntities;
    }

    @Transactional
    public void deleteUploadedFiles(Long postId)
    {
        List<FileEntity> fileEntities = fileRepository.findByPostId(postId);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    public void deleteUploadedFiles(List<String> urls)
    {
        List<FileEntity> fileEntities = fileRepository.findAllByUrl(urls);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    public void deleteProfilePhoto(Long memberId)
    {
        Optional<FileEntity> file = fileRepository.findByMemberId(memberId);

        if(file.isPresent())
        {
            log.info("file removed successfully = {}", file.get().getStoredFileName());
            fileRepository.deleteById(file.get().getId());
        }
    }

    @Transactional
    public String storeProfileImage(MultipartFile multipartFile, MemberEntity memberEntity)
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

        FileEntity file = FileEntity.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .url(getFileUrl(storeFileName, location))
                .contentType(extractContentType(multipartFile))
                .build();

        file.addMember(memberEntity);
        return fileRepository.save(file).getUrl();
    }

    public List<String> getFileUrlByPostId(Long postId)
    {
        return fileRepository.findUrlByPostId(postId);
    }



    private Optional<FileEntity> getFileByMemberId(Long memberId)
    {
        return fileRepository.findByMemberId(memberId);
    }

    private void deleteFiles(List<FileEntity> fileEntities)
    {
        if(fileEntities != null && !fileEntities.isEmpty())
        {
            fileEntities.forEach(img -> {
                amazonS3.deleteObject(bucket, "postEntity/" + img.getStoredFileName());
                log.info("file removed successfully = {}", img.getStoredFileName());
            });
        }
    }


    private FileEntity storeFileInPost(MultipartFile multipartFile, PostEntity post)
    {
        String uploadFileName = multipartFile.getOriginalFilename();
        String storeFileName = filenameGenerator.createStoreFileName(uploadFileName);
        String location = "postEntity/";

        try{
            saveUploadFile(storeFileName, multipartFile, location);
        } catch (IOException e){
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }

        FileEntity file = FileEntity.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .url(getFileUrl(storeFileName, location))
                .contentType(extractContentType(multipartFile))
                .build();


        FileEntity filePS = fileRepository.save(file);
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