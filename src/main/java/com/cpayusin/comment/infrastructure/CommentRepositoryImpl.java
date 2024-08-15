package com.cpayusin.comment.infrastructure;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.service.port.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository
{
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<CommentEntity> findAllByPostEntityId(Long postId)
    {
        return commentJpaRepository.findAllByPostEntityId(postId);
    }

    @Override
    public Optional<CommentEntity> findByIdWithOptimisticLock(Long commentId)
    {
        return commentJpaRepository.findByIdWithOptimisticLock(commentId);
    }

    @Override
    public List<CommentEntity> findParentCommentsByPostId(Long postId, String commentType)
    {
        return commentJpaRepository.findParentCommentsByPostId(postId, commentType);
    }

    @Override
    public Page<CommentEntity> findParentCommentsByPostId(Long postId, String commentType, Pageable pageable)
    {
        return commentJpaRepository.findParentCommentsByPostId(postId, commentType, pageable);
    }

    @Override
    public List<CommentEntity> findChildCommentsByPostId(Long postId)
    {
        return List.of();
    }

    @Override
    public Page<CommentResponseForProfile> findCommentsForProfile(Long memberId, Pageable pageable)
    {
        return null;
    }

    @Override
    public Optional<CommentEntity> findById(Long commentId)
    {
        return commentJpaRepository.findById(commentId);
    }

    @Override
    public CommentEntity save(CommentEntity comment)
    {
        return commentJpaRepository.save(comment);
    }

    @Override
    public void deleteById(Long commentId)
    {
        commentJpaRepository.deleteById(commentId);
    }

    @Override
    public void deleteAllInBatch(List<CommentEntity> commentEntities)
    {
        commentJpaRepository.deleteAllInBatch(commentEntities);
    }

    @Override
    public boolean existsById(Long commentId)
    {
        return commentJpaRepository.existsById(commentId);
    }
}
