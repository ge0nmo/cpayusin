package com.cpayusin.common.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FilenameGenerator
{
    public String createStoreFileName(String originalFileName)
    {
        String uniqueFilename = UUID.randomUUID().toString();

        String fileType = extractType(originalFileName);

        return uniqueFilename + "." + fileType;
    }

    public String extractType(String filename)
    {
        int location = filename.lastIndexOf('.');

        return filename.substring(location + 1);
    }
}
