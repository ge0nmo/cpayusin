package com.cpayusin.service.impl;

import com.cpayusin.global.dto.PageInfo;
import com.cpayusin.global.exception.BusinessLogicException;
import com.cpayusin.global.exception.ExceptionMessage;
import com.cpayusin.mapper.CommentMapper;
import com.cpayusin.model.Comment;
import com.cpayusin.model.Member;
import com.cpayusin.model.Post;
import com.cpayusin.model.type.CommentType;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.payload.request.comment.CommentUpdateRequest;
import com.cpayusin.payload.response.GlobalResponse;
import com.cpayusin.payload.response.comment.*;
import com.cpayusin.repository.CommentRepository;
import com.cpayusin.service.CommentService;
import com.cpayusin.service.PostService;
import com.cpayusin.service.UtilService;
import com.cpayusin.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService
{
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UtilService authService;
    private final VoteService voteService;

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public CommentCreatedResponse saveComment(CommentCreateRequest request, Member currentMember)
    {
        Post post = postService.findByIdWithOptimisticLock(request.getPostId());
        Comment comment = CommentMapper.INSTANCE.toCommentEntity(request);
        comment.addPost(post);
        comment.addMember(currentMember);
        if (request.getParentCommentId() != null) {
            Comment parent = getComment(request.getParentCommentId());
            checkIfPostHasExactComment(post, parent);
            if (parent.getParent() != null) {
                throw new BusinessLogicException(ExceptionMessage.COMMENT_ALREADY_NESTED);
            }
            comment.addParent(parent);
            comment.setType(CommentType.CHILD_COMMENT.getCode());
        }

        post.increaseCommentCount();
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


    public GlobalResponse<CommentMultiResponse> getCommentsByPostId(Long postId, Member member, Pageable pageable)
    {
        Post post = postService.findById(postId);
        Long memberId = member != null ? member.getId() : null;
        Page<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId, CommentType.PARENT_COMMENT.getCode(), pageable);
        List<Comment> childComments = commentRepository.findChildCommentsByPostId(postId);
        Map<Long, List<Comment>> childCommentMap = childComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParent().getId()));
        Page<CommentResponse> commentResponses = mapCommentsToResponse(parentComments, memberId, childCommentMap);
        CommentMultiResponse response = new CommentMultiResponse();
        response.setPostId(postId);
        response.setPostTitle(post.getTitle());
        response.setBoardId(post.getBoard().getId());
        response.setBoardName(post.getBoard().getName());
        response.setComments(commentResponses.getContent());
        return new GlobalResponse<>(response, PageInfo.of(commentResponses));
    }

    private Page<CommentResponse> mapCommentsToResponse(Page<Comment> parentComments, Long memberId, Map<Long, List<Comment>> childCommentMap)
    {
        return parentComments.map(comment ->
        {
            CommentResponse parentResponse = CommentMapper.INSTANCE.toCommentParentResponse(comment);
            boolean parentVoteStatus = voteService.checkIfMemberVotedComment(memberId, comment.getId());
            parentResponse.setVoteStatus(parentVoteStatus);
            List<CommentChildrenResponse> childrenResponse = Optional.ofNullable(childCommentMap.get(comment.getId()))
                    .map(children -> children.stream()
                            .map(child ->
                            {
                                CommentChildrenResponse childResponse = CommentMapper.INSTANCE.toCommentChildrenResponse(child);
                                boolean childVoteStatus = voteService.checkIfMemberVotedComment(memberId, child.getId());
                                childResponse.setVoteStatus(childVoteStatus);
                                return childResponse;
                            }).collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            parentResponse.setChildren(childrenResponse);
            return parentResponse;
        });
    }

    public Page<CommentResponseForProfile> getAllCommentsForProfile(Member member, Pageable pageable)
    {
        return commentRepository.findCommentsForProfile(member.getId(), pageable);
    }

    public CommentSingleResponse getCommentSingleResponse(Long commentId, Member member)
    {
        Comment comment = getComment(commentId);
        boolean voteStatus = false;
        if (member != null)
            voteStatus = voteService.checkIfMemberVotedComment(member.getId(), comment.getId());
        return CommentMapper.INSTANCE.toCommentSingleResponse(comment, voteStatus);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public boolean deleteComment(Long commentId, Member currentMember)
    {
        Comment comment = getComment(commentId);
        Post post = comment.getPost();
        authService.checkPermission(comment.getMember().getId(), currentMember);
        if (comment.getChildren().isEmpty()) {
            voteService.deleteAllVoteInTheComment(commentId);
            commentRepository.deleteById(commentId);
            post.decreaseCommentCount();
            return !commentRepository.existsById(commentId);
        } else {
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
        if (comment.getPost() != post)
            throw new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND);
    }

}
