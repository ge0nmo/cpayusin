package com.cpayusin.file.infrastructure;

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
    public List<FileEntity> findByPostId(Long postId)
    {
        return fileJpaRepository.findByPostId(postId);
    }

    @Override
    public Optional<FileEntity> findByMemberId(Long memberId)
    {
        return fileJpaRepository.findByMemberId(memberId);
    }

    @Override
    public List<String> findUrlByPostId(Long postId)
    {
        return fileJpaRepository.findUrlByPostId(postId);
    }

    @Override
    public List<FileEntity> findAllByUrl(List<String> urls)
    {
        return fileJpaRepository.findAllByUrl(urls);
    }

    @Override
    public FileEntity save(FileEntity fileEntity)
    {
        return fileJpaRepository.save(fileEntity);
    }

    @Override
    public Optional<FileEntity> findById(Long id)
    {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id)
    {
        fileJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<FileEntity> fileEntities)
    {
        fileJpaRepository.deleteAll(fileEntities);
    }
}
