package com.cpayusin.vote.service;

import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.infrastructure.VoteEntity;
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
    public boolean votePost(MemberEntity currentMember, Long postId)
    {
        PostEntity post = findByPostId(postId);
        Optional<VoteEntity> optionalVote = voteRepository.findByMemberIdAndPostId(currentMember.getId(), postId);

        if(optionalVote.isPresent()) {
            voteRepository.deleteById(optionalVote.get().getId());
            post.downVote();
            return false;
        } else {
            voteRepository.save(VoteEntity.votePost(currentMember, post));
            post.upVote();
            return true;
        }
    }

    @Transactional
    public boolean voteComment(MemberEntity currentMember, Long commentId)
    {
        CommentEntity commentEntity = findByCommentId(commentId);
        Optional<VoteEntity> optionalVote = voteRepository.findByMemberIdAndCommentId(currentMember.getId(), commentId);

        if(optionalVote.isPresent()) {
            voteRepository.deleteById(optionalVote.get().getId());
            commentEntity.downVote();
            return false;
        }
        else {
            voteRepository.save(VoteEntity.voteComment(currentMember, commentEntity));
            commentEntity.upVote();
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

    private PostEntity findByPostId(Long postId){
        return postRepository.findByIdWithOptimisticLock(postId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND));
    }

    private CommentEntity findByCommentId(Long commentId)
    {
        return commentRepository.findByIdWithOptimisticLock(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.COMMENT_NOT_FOUND));
    }
}
