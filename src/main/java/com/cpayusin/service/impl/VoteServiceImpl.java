package com.cpayusin.service.impl;

import com.cpayusin.global.exception.BusinessLogicException;
import com.cpayusin.global.exception.ExceptionMessage;
import com.cpayusin.model.Comment;
import com.cpayusin.model.Member;
import com.cpayusin.model.Post;
import com.cpayusin.model.Vote;
import com.cpayusin.repository.CommentRepository;
import com.cpayusin.repository.PostRepository;
import com.cpayusin.repository.VoteRepository;
import com.cpayusin.service.VoteService;
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
        Optional<Vote> optionalVote = voteRepository.findByMemberAndPost(currentMember, post);

        if(optionalVote.isPresent()) {
            voteRepository.delete(optionalVote.get());
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
        Optional<Vote> optionalVote = voteRepository.findByMemberAndComment(currentMember, comment);

        if(optionalVote.isPresent()) {
            voteRepository.delete(optionalVote.get());
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

        return voteRepository.existsVoteByMemberIdAndPostId(memberId, postId);
    }

    public boolean checkIfMemberVotedComment(Long memberId, Long commentId)
    {
        if(memberId == null)
            return false;

        return voteRepository.existsVoteByMemberIdAndCommentId(memberId, commentId);
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
