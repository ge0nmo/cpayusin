package com.cpayusin.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cpayusin.common.utils.FilenameGenerator;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.controller.response.FileResponse;
import com.cpayusin.file.domain.File;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.mapper.FileMapper;
import com.cpayusin.file.service.port.FileRepository;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileServiceImpl implements FileService
{
    private final FileRepository fileRepository;
    private final FilenameGenerator filenameGenerator;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.url}")
    private String cloudfrontUrl;

    @Value("${cloud.aws.prefix}")
    private String prefix;


    @Override
    public FileResponse save(MultipartFile multipartFile)
    {
        validateImageFile(multipartFile);
        String uniqueFilename = filenameGenerator.createStoreFileName(multipartFile.getOriginalFilename());

        String fileUrl = saveUploadFile(uniqueFilename, multipartFile);

        File file = FileMapper.INSTANCE.toFileFile(multipartFile.getOriginalFilename(), uniqueFilename, fileUrl, extractType(uniqueFilename));

        file = fileRepository.save(file);

        return FileMapper.INSTANCE.toFileResponse(file);
    }


    private String saveUploadFile(String uniqueFilename, MultipartFile file)
    {
        try {
            String contentType = extractType(file.getOriginalFilename());
            ObjectMetadata metadata = new ObjectMetadata();

            String objectName = getObjectFilename(uniqueFilename);

            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucket, objectName, file.getInputStream(), metadata);

            return String.valueOf(amazonS3.getUrl(bucket, objectName));

        } catch (IOException e) {
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }
    }


    private String extractType(String filename)
    {
        if(!StringUtils.hasLength(filename)){
            throw new BusinessLogicException(ExceptionMessage.FILE_NOT_STORED);
        }

        int location = filename.lastIndexOf('.');
        String fileType = filename.substring(location + 1);
        log.info("file type = {}", fileType);
        return fileType;
    }

    private void validateImageFile(MultipartFile file)
    {
        String contentType = file.getContentType();
        if (!StringUtils.hasLength(contentType) || !contentType.contains("image")) {
            throw new BusinessLogicException(ExceptionMessage.EXT_NOT_ACCEPTED);
        }
    }

    private String getObjectFilename(String uniqueFilename)
    {
        return prefix + "/" + uniqueFilename;
    }

}