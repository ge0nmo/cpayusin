package com.cpayusin.file.infrastructure;

import com.cpayusin.file.domain.FileDomain;
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
    public List<FileDomain> findByPostId(Long postId)
    {
        return fileJpaRepository.findByPostId(postId)
                .stream()
                .map(File::toModel)
                .toList();
    }

    @Override
    public Optional<FileDomain> findByMemberId(Long memberId)
    {
        return fileJpaRepository.findByMemberId(memberId)
                .map(File::toModel);
    }

    @Override
    public List<String> findUrlByPostId(Long postId)
    {
        return fileJpaRepository.findUrlByPostId(postId);
    }

    @Override
    public List<FileDomain> findAllByUrl(List<String> urls)
    {
        return fileJpaRepository.findAllByUrl(urls).stream()
                .map(File::toModel)
                .toList();
    }

    @Override
    public FileDomain save(FileDomain file)
    {
        return fileJpaRepository.save(File.from(file)).toModel();
    }

    @Override
    public Optional<FileDomain> findById(Long id)
    {
        return fileJpaRepository.findById(id).map(File::toModel);
    }

    @Override
    public void deleteById(Long id)
    {
        fileJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<FileDomain> fileEntities)
    {
        List<File> files = fileEntities.stream()
                .map(File::from)
                .toList();

        fileJpaRepository.deleteAll(files);
    }
}
