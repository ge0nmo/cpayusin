package com.cpayusin.file.repository;

import com.cpayusin.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileJpaRepository extends JpaRepository<File, Long>
{
    @Query("SELECT f FROM File f WHERE f.post.id = :postId")
    List<File> findByPostId(Long postId);

    @Query("SELECT f FROM File f WHERE f.member.id = :memberId ")
    Optional<File> findByMemberId(Long memberId);

    @Query("SELECT f.url FROM File f WHERE f.post.id = :postId")
    List<String> findUrlByPostId(@Param("postId") Long postId);

    @Query("SELECT f FROM File f WHERE f.url IN :urls")
    List<File> findAllByUrl(@Param("urls") List<String> urls);


}
