package com.cpayusin.vote.service;

import com.cpayusin.comment.infrastructure.Comment;
import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.post.infrastructure.Post;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.infrastructure.Vote;
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
    private final CommentRepository commentRepository;

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

    @Transactional
    public boolean voteComment(Member currentMember, Long commentId)
    {
        Comment comment = findByCommentId(commentId);
        Optional<Vote> optionalVote = voteRepository.findByMemberIdAndCommentId(currentMember.getId(), commentId);

        if(optionalVote.isPresent()) {
            voteRepository.deleteById(optionalVote.get().getId());
            comment.downVote();
            return false;
        }
        else {
            voteRepository.save(Vote.voteComment(currentMember, comment));
            comment.upVote();
            return true;
        }
    }


    public void deleteAllVoteInThePost(Long postId)
    {
        voteRepository.deleteAllInBatch(voteRepository.findAllByPostId(postId));
    }

    public void deleteAllVoteInTheComment(Long commentId)
    {
        voteRepository.deleteAllInBatch(voteRepository.findAllByCommentId(commentId));
    }


    public boolean checkIfMemberVotedPost(Long memberId, Long postId)
    {
        if(memberId == null)
            return false;

        return voteRepository.existsVoteByMemberEntityIdAndPostEntityId(memberId, postId);
    }

    public boolean checkIfMemberVotedComment(Long memberId, Long commentId)
    {
        if(memberId == null)
            return false;

        return voteRepository.existsVoteByMemberEntityIdAndCommentEntityId(memberId, commentId);
    }

    private Post findByPostId(Long postId){
        return postRepository.findByIdWithOptimisticLock(postId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND));
    }

    private Comment findByCommentId(Long commentId)
    {
        return commentRepository.findByIdWithOptimisticLock(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.COMMENT_NOT_FOUND));
    }
}
