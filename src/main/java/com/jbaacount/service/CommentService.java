package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.mapper.CommentMapper;
import com.jbaacount.model.Comment;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import com.jbaacount.model.type.CommentType;
import com.jbaacount.payload.request.comment.CommentCreateRequest;
import com.jbaacount.payload.request.comment.CommentUpdateRequest;
import com.jbaacount.payload.response.comment.*;
import com.jbaacount.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService
{
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UtilService authService;
    private final VoteService voteService;

    @Transactional
    public CommentCreatedResponse saveComment(CommentCreateRequest request, Member currentMember)
    {
        Post post = postService.getPostById(request.getPostId());
        Comment comment = CommentMapper.INSTANCE.toCommentEntity(request);

        comment.addPost(post);
        comment.addMember(currentMember);
        if(request.getParentCommentId() != null)
        {
            Comment parent = getComment(request.getParentCommentId());
            checkIfPostHasExactComment(post, parent);
            if(parent.getParent() != null)
            {
                throw new BusinessLogicException(ExceptionMessage.COMMENT_ALREADY_NESTED);
            }

            comment.addParent(parent);
            comment.setType(CommentType.CHILD_COMMENT.getCode());
        }

        if(post.getMember() != currentMember)
        {
            currentMember.getScoreByComment();
        }

        return CommentMapper.INSTANCE.toCommentCreatedResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentUpdateResponse updateComment(CommentUpdateRequest request, Long commentId, Member currentMember)
    {
        Comment comment = getComment(commentId);
        authService.isTheSameUser(comment.getMember().getId(), currentMember.getId());

        Optional.ofNullable(request.getText())
                .ifPresent(comment::updateText);

        return CommentMapper.INSTANCE.toCommentUpdateResponse(comment);
    }

    public Comment getComment(Long commentId)
    {
        return commentRepository.findById(commentId).orElseThrow(() -> new BusinessLogicException(ExceptionMessage.COMMENT_NOT_FOUND));
    }


    public List<CommentMultiResponse> getAllCommentByPostId(Long postId, Member member)
    {
        List<Comment> parentCommentsByPostId = commentRepository.findParentCommentsByPostId(postId, CommentType.PARENT_COMMENT.getCode());
        var parentList  = CommentMapper.INSTANCE.toCommentParentResponseList(parentCommentsByPostId);

        for (CommentMultiResponse parent : parentList)
        {
            boolean parentVoteStatus = voteService.checkIfMemberVotedComment(member.getId(), parent.getId());
            parent.setVoteStatus(parentVoteStatus);

            for (CommentChildrenResponse child : parent.getChildren())
            {
                boolean childVoteStatus = voteService.checkIfMemberVotedComment(member.getId(), child.getId());

                child.setVoteStatus(childVoteStatus);
            }
        }

        return parentList;
    }

    public Page<CommentResponseForProfile> getAllCommentsForProfile(Member member, Pageable pageable)
    {
        return commentRepository.findCommentsForProfile(member.getId(), pageable);
    }

    public CommentSingleResponse getCommentSingleResponse(Long commentId, Member member)
    {
        Comment comment = getComment(commentId);

        boolean voteStatus = false;

        if(member != null)
            voteStatus = voteService.checkIfMemberVotedComment(member.getId(), comment.getId());

        return CommentMapper.INSTANCE.toCommentSingleResponse(comment, voteStatus);
    }

    @Transactional
    public boolean deleteComment(Long commentId, Member currentMember)
    {
        Comment comment = getComment(commentId);
        authService.checkPermission(comment.getMember().getId(), currentMember);

        if(comment.getChildren().isEmpty())
        {
            voteService.deleteAllVoteInTheComment(commentId);
            commentRepository.deleteById(commentId);
            return !commentRepository.existsById(commentId);
        }

        else
        {
            comment.deleteComment();
            return true;
        }
    }

    @Transactional
    public void deleteAllByPostId(Long postId)
    {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        commentList
                .forEach(comment -> {
                    voteService.deleteAllVoteInTheComment(comment.getId());
                });

        commentRepository.deleteAllInBatch(commentList);
    }

    private void checkIfPostHasExactComment(Post post, Comment comment)
    {
        if(comment.getPost() != post)
            throw new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND);
    }
}
