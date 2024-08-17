package com.cpayusin.comment.repository;

import com.cpayusin.comment.controller.response.CommentResponseForProfile;
import com.cpayusin.comment.domain.Comment;
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
    public List<Comment> findAllByPostId(Long postId)
    {
        return commentJpaRepository.findAllByPostId(postId);
    }

    @Override
    public Optional<Comment> findByIdWithOptimisticLock(Long commentId)
    {
        return commentJpaRepository.findByIdWithOptimisticLock(commentId);
    }


    @Override
    public Page<Comment> findParentCommentsByPostId(Long postId, String commentType, Pageable pageable)
    {
        return commentJpaRepository.findParentCommentsByPostId(postId, commentType, pageable);
    }

    @Override
    public List<Comment> findChildCommentsByPostId(Long postId)
    {
        return commentJpaRepository.findChildCommentsByPostId(postId);
    }

    @Override
    public Page<CommentResponseForProfile> findCommentsForProfile(Long memberId, Pageable pageable)
    {
        return null;
    }

    @Override
    public Optional<Comment> findById(Long commentId)
    {
        return commentJpaRepository.findById(commentId);
    }

    @Override
    public Comment save(Comment comment)
    {
        return commentJpaRepository.save(comment);
    }

    @Override
    public void deleteById(Long commentId)
    {
        commentJpaRepository.deleteById(commentId);
    }

    @Override
    public void deleteAllInBatch(List<Comment> commentEntities)
    {
        commentJpaRepository.deleteAllInBatch(commentEntities);
    }

    @Override
    public boolean existsById(Long commentId)
    {
        return commentJpaRepository.existsById(commentId);
    }

    @Override
    public List<Long> findAllByPostId(long postId)
    {
        return commentJpaRepository.findAllByPostId(postId);
    }
}
