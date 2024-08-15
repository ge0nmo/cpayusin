package com.cpayusin.file.service.port;

import com.cpayusin.file.domain.FileDomain;
import com.cpayusin.file.infrastructure.File;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository
{
    List<FileDomain> findByPostId(Long postId);

    Optional<FileDomain> findByMemberId(Long memberId);

    List<String> findUrlByPostId(@Param("postId") Long postId);

    List<FileDomain> findAllByUrl(@Param("urls") List<String> urls);

    FileDomain save(FileDomain file);

    Optional<FileDomain> findById(Long id);

    void deleteById(Long id);

    void deleteAll(List<FileDomain> fileEntities);
}
