package com.cpayusin.post.infrastructure;

import com.cpayusin.post.controller.response.PostMultiResponse;
import com.cpayusin.post.controller.response.PostResponseForProfile;
import com.cpayusin.post.controller.response.PostResponseProjection;
import com.cpayusin.post.domain.PostDomain;
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
    public List<PostDomain> findAllByBoardId(Long boardId)
    {
        return postJpaRepository.findAllByBoardId(boardId).stream()
                .map(Post::toModel)
                .toList();
    }

    @Override
    public Optional<PostDomain> findByIdWithOptimisticLock(Long id)
    {
        return postJpaRepository.findByIdWithOptimisticLock(id)
                .map(Post::toModel);
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
    public Page<PostDomain> findAllByBoardId(Long boardId, String keyword, Pageable pageable)
    {
        return postJpaRepository.findAllByBoardId(boardId, keyword, pageable)
                .map(Post::toModel);
    }

    @Override
    public Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable)
    {
        return postJpaRepository.findAllPostsByBoardIds(boardIds, lastPost, pageable);
    }

    @Override
    public PostDomain save(PostDomain post)
    {
        return postJpaRepository.save(Post.from(post))
                .toModel();
    }

    @Override
    public List<PostDomain> saveAll(List<PostDomain> postDomains)
    {
        List<Post> posts = postDomains.stream()
                .map(Post::from)
                .toList();

        return postJpaRepository.saveAll(posts).stream()
                .map(Post::toModel)
                .toList();
    }

    @Override
    public Optional<PostDomain> findById(Long id)
    {
        return postJpaRepository.findById(id)
                .map(Post::toModel);
    }

    @Override
    public void deleteById(Long id)
    {
        postJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllInBatch(List<PostDomain> postDomains)
    {
        postJpaRepository.deleteAllInBatch(postDomains.stream()
                .map(Post::from)
                .toList());
    }

    @Override
    public boolean existsById(Long id)
    {
        return postJpaRepository.existsById(id);
    }
}
