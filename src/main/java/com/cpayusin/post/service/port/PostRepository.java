package com.cpayusin.post.service.port;

import com.cpayusin.post.controller.response.PostMultiResponse;
import com.cpayusin.post.controller.response.PostResponseForProfile;
import com.cpayusin.post.controller.response.PostResponseProjection;
import com.cpayusin.post.infrastructure.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository
{
    List<Post> findAllByBoardId(@Param("boardId") Long boardId);

    Optional<Post> findByIdWithOptimisticLock(@Param("id") Long id);

    Page<PostResponseProjection> findAllPostByBoardId(@Param("boardIds") List<Long> boardIds,
                                                      Pageable pageable);
    Page<PostResponseForProfile> findAllByMemberIdForProfile(@Param("memberId") Long memberId, Pageable pageable);

    Page<Post> findAllByBoardId(@Param("boardId") Long boardId,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable);

    Post save(Post post);

    List<Post> saveAll(List<Post> posts);

    Optional<Post> findById(Long id);

    void deleteById(Long id);

    void deleteAllInBatch(List<Post> postEntities);

    boolean existsById(Long id);
}
