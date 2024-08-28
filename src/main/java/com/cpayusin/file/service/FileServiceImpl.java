package com.cpayusin.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cpayusin.common.utils.FilenameGenerator;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.domain.File;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.service.port.FileRepository;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
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
    public File save(File file)
    {
        return fileRepository.save(file);
    }

    @Transactional
    public File saveForOauth2(String picture, Member member)
    {
        File file = File
                .builder()
                .uploadFileName(UUID.randomUUID().toString())
                .storeFileName(UUID.randomUUID().toString())
                .url(picture)
                .contentType(UUID.randomUUID().toString())
                .build();

        file.addMember(member);

        return fileRepository.save(file);
    }

    @Transactional
    public File updateForOAuth2(String picture, Member member)
    {
        File file = getFileByMemberId(member.getId())
                .orElseGet(() -> {
                    return saveForOauth2(picture, member);
                });

        file.setUrl(picture);

        return file;
    }


    @Transactional
    public List<File> storeFiles(List<MultipartFile> files, Post post)
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
    public void deleteUploadedFiles(Long postId)
    {
        List<File> fileEntities = fileRepository.findByPostId(postId);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    public void deleteUploadedFiles(List<String> urls)
    {
        List<File> fileEntities = fileRepository.findAllByUrl(urls);

        deleteFiles(fileEntities);

        fileRepository.deleteAll(fileEntities);
    }

    @Transactional
    public void deleteProfilePhoto(Long memberId)
    {
        Optional<File> file = fileRepository.findByMemberId(memberId);

        if(file.isPresent())
        {
            log.info("file removed successfully = {}", file.get().getStoredFileName());
            fileRepository.deleteById(file.get().getId());
        }
    }

    @Transactional
    public String storeProfileImage(MultipartFile multipartFile, Member member)
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
                .contentType(extractType(storeFileName))
                .build();

        file.addMember(member);
        return fileRepository.save(file).getUrl();
    }

    public List<String> getFileUrlByPostId(Long postId)
    {
        return fileRepository.findUrlByPostId(postId);
    }



    private Optional<File> getFileByMemberId(Long memberId)
    {
        return fileRepository.findByMemberId(memberId);
    }

    private void deleteFiles(List<File> fileEntities)
    {
        if(fileEntities != null && !fileEntities.isEmpty())
        {
            fileEntities.forEach(img -> {
                amazonS3.deleteObject(bucket, "post/" + img.getStoredFileName());
                log.info("file removed successfully = {}", img.getStoredFileName());
            });
        }
    }


    private File storeFileInPost(MultipartFile multipartFile, Post post)
    {

        String uniqueFilename = filenameGenerator.createStoreFileName(multipartFile.getOriginalFilename());

        String location = "post/";

        try{
            saveUploadFile(uniqueFilename, multipartFile, location);
        } catch (IOException e){
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }

        File file = File.builder()
                .uploadFileName(multipartFile.getOriginalFilename())
                .storeFileName(uniqueFilename)
                .url(getFileUrl(uniqueFilename, location))
                .contentType(extractType(uniqueFilename))
                .build();


        File filePS = fileRepository.save(file);
        filePS.addPost(post);

        return filePS;
    }

    private void saveUploadFile(String storeFileName, MultipartFile file, String location) throws IOException
    {
        String contentType = extractType(file.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, location + storeFileName, file.getInputStream(), metadata);
    }

    private String getFileUrl(String fileName, String location)
    {
        return amazonS3.getUrl(bucket,  location + fileName).toString();
    }

    private String extractType(String filename)
    {
        int location = filename.lastIndexOf('.');

        String fileType = filename.substring(location + 1);

        log.info("file type = {}", fileType);
        return fileType;
    }

}