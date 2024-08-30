package com.cpayusin.file.service.port;

import com.cpayusin.file.domain.File;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository
{
    List<File> findByPostId(Long postId);

    Optional<File> findByMemberId(Long memberId);

    List<String> findUrlByPostId(@Param("postId") Long postId);

    List<File> findAllByUrl(@Param("urls") List<String> urls);

    File save(File file);

    Optional<File> findById(Long id);

    void deleteById(Long id);

    void deleteAll(List<File> fileEntities);

    void deleteAllByPostId(Long postId);
}
