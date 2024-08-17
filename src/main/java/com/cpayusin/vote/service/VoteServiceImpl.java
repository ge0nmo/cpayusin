package com.cpayusin.vote.service;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.domain.Vote;
import com.cpayusin.vote.controller.port.VoteService;
import com.cpayusin.vote.service.port.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoteServiceImpl implements VoteService
{
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean votePost(Member currentMember, Long postId)
    {
        Post post = findByPostId(postId);
        Optional<Vote> optionalVote = voteRepository.findByMemberIdAndPostId(currentMember.getId(), postId);

        if(optionalVote.isPresent()) {
            voteRepository.deleteById(optionalVote.get().getId());
            post.downVote();
            return false;
        } else {
            voteRepository.save(Vote.votePost(currentMember, post));
            post.upVote();
            return true;
        }
    }

    @Override
    public void deleteAllVoteInThePost(Long postId)
    {
        voteRepository.deleteAllByPostId(postId);
    }

    @Override
    public boolean checkIfMemberVotedPost(Long memberId, Long postId)
    {
        if(memberId == null)
            return false;

        return voteRepository.existsVoteByMemberIdAndPostId(memberId, postId);
    }

    @Override
    public void deleteAllByPostId(Long postId)
    {
        voteRepository.deleteAllByPostId(postId);
    }

    private Post findByPostId(Long postId){
        return postRepository.findByIdWithOptimisticLock(postId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND));
    }

}
