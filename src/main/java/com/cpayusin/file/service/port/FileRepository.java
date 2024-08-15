package com.cpayusin.file.service.port;

import com.cpayusin.file.infrastructure.FileEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository
{
    List<FileEntity> findByPostId(Long postId);

    Optional<FileEntity> findByMemberId(Long memberId);

    List<String> findUrlByPostId(@Param("postId") Long postId);

    List<FileEntity> findAllByUrl(@Param("urls") List<String> urls);

    FileEntity save(FileEntity fileEntity);

    Optional<FileEntity> findById(Long id);

    void deleteById(Long id);

    void deleteAll(List<FileEntity> fileEntities);
}
