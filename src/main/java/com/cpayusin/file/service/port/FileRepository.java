package com.cpayusin.file.service.port;

import com.cpayusin.file.domain.File;

import java.util.Optional;

public interface FileRepository
{
    File save(File file);

    Optional<File> findById(Long id);

    void deleteById(Long id);
}
