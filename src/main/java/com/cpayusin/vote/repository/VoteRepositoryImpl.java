package com.cpayusin.vote.repository;

import com.cpayusin.vote.domain.Vote;
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
    public Optional<Vote> findByMemberIdAndPostId(Long memberId, Long postId)
    {
        return voteJpaRepository.findByMemberIdAndPostId(memberId, postId);
    }

    @Override
    public List<Vote> findAllByPostId(Long postId)
    {
        return voteJpaRepository.findAllByPostId(postId);
    }

    @Override
    public boolean existsVoteByMemberIdAndPostId(Long memberId, Long postId)
    {
        return voteJpaRepository.existsVoteByMemberIdAndPostId(memberId, postId);
    }

    @Override
    public Optional<Vote> findById(Long id)
    {
        return voteJpaRepository.findById(id);
    }

    @Override
    public Vote save(Vote vote)
    {
        return voteJpaRepository.save(vote);
    }

    @Override
    public void deleteById(Long id)
    {
        voteJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByPostId(long postId)
    {
        voteJpaRepository.deleteByPostId(postId);
    }
}
