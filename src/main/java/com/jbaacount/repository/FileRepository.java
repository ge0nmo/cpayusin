package com.jbaacount.repository;

import com.jbaacount.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long>
{
    List<File> findByPostId(Long postId);

    Optional<File> findByMemberId(Long memberId);

    @Query("SELECT f.url FROM File f WHERE f.post.id = :postId")
    List<String> findUrlByPostId(@Param("postId") Long postId);


    @Query("SELECT f FROM File f WHERE f.url IN :urls")
    List<File> findAllByUrl(@Param("urls") List<String> urls);
}
