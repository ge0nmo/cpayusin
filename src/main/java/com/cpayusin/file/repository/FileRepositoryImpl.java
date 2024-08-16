package com.cpayusin.file.repository;

import com.cpayusin.file.domain.File;
import com.cpayusin.file.service.port.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FileRepositoryImpl implements FileRepository
{
    private final FileJpaRepository fileJpaRepository;

    @Override
    public List<File> findByPostId(Long postId)
    {
        return fileJpaRepository.findByPostId(postId);
    }

    @Override
    public Optional<File> findByMemberId(Long memberId)
    {
        return fileJpaRepository.findByMemberId(memberId);
    }

    @Override
    public List<String> findUrlByPostId(Long postId)
    {
        return fileJpaRepository.findUrlByPostId(postId);
    }

    @Override
    public List<File> findAllByUrl(List<String> urls)
    {
        return fileJpaRepository.findAllByUrl(urls);
    }

    @Override
    public File save(File file)
    {
        return fileJpaRepository.save(file);
    }

    @Override
    public Optional<File> findById(Long id)
    {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id)
    {
        fileJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<File> fileEntities)
    {
        fileJpaRepository.deleteAll(fileEntities);
    }
}
