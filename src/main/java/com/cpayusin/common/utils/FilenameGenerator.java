package com.cpayusin.common.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FilenameGenerator
{
    public String createStoreFileName(String originalFileName)
    {
        String uuid = UUID.randomUUID().toString();

        String ext = extractedEXT(originalFileName);

        return uuid + "." + ext;
    }

    public String extractedEXT(String originalFileName)
    {
        int num = originalFileName.lastIndexOf(".");

        String ext = originalFileName.substring(num + 1);

        return ext;
    }
}
