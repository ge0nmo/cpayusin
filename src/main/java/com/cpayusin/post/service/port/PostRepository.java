package com.cpayusin.post.service.port;

import com.cpayusin.post.controller.response.PostMultiResponse;
import com.cpayusin.post.controller.response.PostResponseForProfile;
import com.cpayusin.post.controller.response.PostResponseProjection;
import com.cpayusin.post.infrastructure.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository
{
    List<PostEntity> findAllByBoardId(@Param("boardId") Long boardId);

    Optional<PostEntity> findByIdWithOptimisticLock(@Param("id") Long id);

    Page<PostResponseProjection> findAllPostByBoardId(@Param("boardIds") List<Long> boardIds,
                                                      Pageable pageable);
    Page<PostResponseForProfile> findAllByMemberIdForProfile(@Param("memberId") Long memberId, Pageable pageable);

    Page<PostEntity> findAllByBoardId(@Param("boardId") Long boardId,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable);

    PostEntity save(PostEntity post);

    List<PostEntity> saveAll(List<PostEntity> posts);

    Optional<PostEntity> findById(Long id);

    void deleteById(Long id);

    void deleteAllInBatch(List<PostEntity> postEntities);

    boolean existsById(Long id);
}
