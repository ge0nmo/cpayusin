package com.cpayusin.post.repository;

import com.cpayusin.post.controller.response.PostMultiResponse;
import com.cpayusin.post.controller.response.PostResponseForProfile;
import com.cpayusin.post.controller.response.PostResponseProjection;
import com.cpayusin.post.domain.Post;
import com.cpayusin.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepository
{
    private final PostJpaRepository postJpaRepository;

    @Override
    public List<Post> findAllByBoardId(Long boardId)
    {
        return postJpaRepository.findAllByBoardId(boardId);
    }

    @Override
    public Optional<Post> findByIdWithOptimisticLock(Long id)
    {
        return postJpaRepository.findByIdWithOptimisticLock(id);
    }

    @Override
    public Page<PostResponseProjection> findAllPostByBoardId(List<Long> boardIds, Pageable pageable)
    {
        return postJpaRepository.findAllPostByBoardId(boardIds, pageable);
    }

    @Override
    public Page<PostResponseForProfile> findAllByMemberIdForProfile(Long memberId, Pageable pageable)
    {
        return postJpaRepository.findAllByMemberIdForProfile(memberId, pageable);
    }

    @Override
    public Page<Post> findAllByBoardId(Long boardId, Pageable pageable)
    {
        return postJpaRepository.findAllByBoardId(boardId, pageable);
    }

    @Override
    public Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable)
    {
        return postJpaRepository.findAllPostsByBoardIds(boardIds, lastPost, pageable);
    }

    @Override
    public Post save(Post post)
    {
        return postJpaRepository.save(post);
    }

    @Override
    public List<Post> saveAll(List<Post> posts)
    {
        return postJpaRepository.saveAll(posts);
    }

    @Override
    public Optional<Post> findById(Long id)
    {
        return postJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id)
    {
        postJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllInBatch(List<Post> postEntities)
    {
        postJpaRepository.deleteAllInBatch(postEntities);
    }

    @Override
    public boolean existsById(Long id)
    {
        return postJpaRepository.existsById(id);
    }
}
