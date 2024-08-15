package com.cpayusin.file.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileJpaRepository extends JpaRepository<FileEntity, Long>
{
    @Query("SELECT f FROM FileEntity f WHERE f.postEntity.id = :postId")
    List<FileEntity> findByPostId(Long postId);

    @Query("SELECT f FROM FileEntity f WHERE f.memberEntity.id = :memberId ")
    Optional<FileEntity> findByMemberId(Long memberId);

    @Query("SELECT f.url FROM FileEntity f WHERE f.postEntity.id = :postId")
    List<String> findUrlByPostId(@Param("postId") Long postId);

    @Query("SELECT f FROM FileEntity f WHERE f.url IN :urls")
    List<FileEntity> findAllByUrl(@Param("urls") List<String> urls);
}
