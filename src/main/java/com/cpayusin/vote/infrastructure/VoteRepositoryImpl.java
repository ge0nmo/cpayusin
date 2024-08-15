package com.cpayusin.vote.infrastructure;

import com.cpayusin.vote.service.port.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VoteRepositoryImpl implements VoteRepository
{
    private final VoteJpaRepository voteJpaRepository;

    @Override
    public Optional<VoteEntity> findByMemberIdAndPostId(Long memberId, Long postId)
    {
        return voteJpaRepository.findByMemberIdAndPostId(memberId, postId);
    }

    @Override
    public Optional<VoteEntity> findByMemberIdAndCommentId(Long memberId, Long commentId)
    {
        return voteJpaRepository.findByMemberIdAndCommentId(memberId, commentId);
    }

    @Override
    public List<VoteEntity> findAllByPostId(Long postId)
    {
        return voteJpaRepository.findAllByPostId(postId);
    }

    @Override
    public List<VoteEntity> findAllByCommentId(Long commentId)
    {
        return voteJpaRepository.findAllByCommentId(commentId);
    }

    @Override
    public boolean existsVoteByMemberEntityIdAndCommentEntityId(Long memberId, Long commentId)
    {
        return voteJpaRepository.existsVoteByMemberEntityIdAndCommentEntityId(memberId, commentId);
    }

    @Override
    public boolean existsVoteByMemberEntityIdAndPostEntityId(Long memberId, Long postId)
    {
        return voteJpaRepository.existsVoteByMemberEntityIdAndPostEntityId(memberId, postId);
    }

    @Override
    public Optional<VoteEntity> findById(Long id)
    {
        return voteJpaRepository.findById(id);
    }

    @Override
    public VoteEntity save(VoteEntity vote)
    {
        return voteJpaRepository.save(vote);
    }

    @Override
    public void deleteById(Long id)
    {
        voteJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllInBatch(List<VoteEntity> votes)
    {
        voteJpaRepository.deleteAllInBatch(votes);
    }
}
