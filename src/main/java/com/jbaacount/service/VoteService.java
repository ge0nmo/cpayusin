package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.model.Comment;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import com.jbaacount.model.Vote;
import com.jbaacount.repository.CommentRepository;
import com.jbaacount.repository.PostRepository;
import com.jbaacount.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoteService
{
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public boolean votePost(Member currentMember, Long postId)
    {
        Post post = findByPostId(postId);
        Optional<Vote> optionalVote = voteRepository.findByMemberAndPost(currentMember, post);

        if(optionalVote.isPresent())
        {
            voteRepository.delete(optionalVote.get());
            post.downVote();
            post.getMember().getScoreByVote(-2);
            return false;
        }

        else
        {
            voteRepository.save(new Vote(currentMember, post));
            post.getMember().getScoreByVote(2);
            post.upVote();
            return true;
        }
    }

    @Transactional
    public boolean voteComment(Member currentMember, Long commentId)
    {
        Comment comment = findByCommentId(commentId);
        Optional<Vote> optionalVote = voteRepository.findByMemberAndComment(currentMember, comment);

        if(optionalVote.isPresent())
        {
            voteRepository.delete(optionalVote.get());
            comment.getMember().getScoreByVote(-2);
            comment.setVoteCount(comment.getVoteCount() - 1);
            return false;
        }

        else
        {
            voteRepository.save(new Vote(currentMember, comment));
            comment.getMember().getScoreByVote(2);
            comment.setVoteCount(comment.getVoteCount() + 1);
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
        return voteRepository.existsVoteByMemberIdAndPostId(memberId, postId);
    }

    public boolean checkIfMemberVotedComment(Long memberId, Long commentId)
    {
        return voteRepository.existsVoteByMemberIdAndCommentId(memberId, commentId);
    }

    private Post findByPostId(Long postId)
    {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND));
    }

    private Comment findByCommentId(Long commentId)
    {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.COMMENT_NOT_FOUND));
    }
}
