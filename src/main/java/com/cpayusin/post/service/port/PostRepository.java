package com.cpayusin.post.service.port;

import com.cpayusin.post.controller.response.PostMultiResponse;
import com.cpayusin.post.controller.response.PostResponseForProfile;
import com.cpayusin.post.controller.response.PostResponseProjection;
import com.cpayusin.post.domain.PostDomain;
import com.cpayusin.post.infrastructure.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository
{
    List<PostDomain> findAllByBoardId(@Param("boardId") Long boardId);

    Optional<PostDomain> findByIdWithOptimisticLock(@Param("id") Long id);

    Page<PostResponseProjection> findAllPostByBoardId(@Param("boardIds") List<Long> boardIds,
                                                      Pageable pageable);
    Page<PostResponseForProfile> findAllByMemberIdForProfile(@Param("memberId") Long memberId, Pageable pageable);

    Page<PostDomain> findAllByBoardId(Long boardId, String keyword, Pageable pageable);

    Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable);

    PostDomain save(PostDomain post);

    List<PostDomain> saveAll(List<PostDomain> posts);

    Optional<PostDomain> findById(Long id);

    void deleteById(Long id);

    void deleteAllInBatch(List<PostDomain> postEntities);

    boolean existsById(Long id);
}
