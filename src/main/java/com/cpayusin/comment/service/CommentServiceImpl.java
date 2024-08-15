package com.cpayusin.comment.service;

import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.request.CommentUpdateRequest;
import com.cpayusin.comment.controller.response.*;
import com.cpayusin.comment.domain.type.CommentType;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.common.service.UtilService;
import com.cpayusin.mapper.CommentMapper;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.vote.controller.port.VoteService;
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
    public CommentCreatedResponse saveComment(CommentCreateRequest request, MemberEntity currentMemberEntity)
    {
        PostEntity post = postService.findByIdWithOptimisticLock(request.getPostId());
        CommentEntity commentEntity = CommentMapper.INSTANCE.toCommentEntity(request);
        commentEntity.addPost(post);
        commentEntity.addMember(currentMemberEntity);
        if (request.getParentCommentId() != null) {
            CommentEntity parent = getComment(request.getParentCommentId());
            checkIfPostHasExactComment(post, parent);
            if (parent.getParent() != null) {
                throw new BusinessLogicException(ExceptionMessage.COMMENT_ALREADY_NESTED);
            }
            commentEntity.addParent(parent);
            commentEntity.setType(CommentType.CHILD_COMMENT.getCode());
        }

        post.increaseCommentCount();
        return CommentMapper.INSTANCE.toCommentCreatedResponse(commentRepository.save(commentEntity));
    }

    @Transactional
    public CommentUpdateResponse updateComment(CommentUpdateRequest request, Long commentId, MemberEntity currentMemberEntity)
    {
        CommentEntity commentEntity = getComment(commentId);
        authService.isTheSameUser(commentEntity.getMemberEntity().getId(), currentMemberEntity.getId());
        Optional.ofNullable(request.getText())
                .ifPresent(commentEntity::updateText);
        return CommentMapper.INSTANCE.toCommentUpdateResponse(commentEntity);
    }

    public CommentEntity getComment(Long commentId)
    {
        return commentRepository.findById(commentId).orElseThrow(() -> new BusinessLogicException(ExceptionMessage.COMMENT_NOT_FOUND));
    }


    public GlobalResponse<CommentMultiResponse> getCommentsByPostId(Long postId, MemberEntity memberEntity, Pageable pageable)
    {
        PostEntity post = postService.findById(postId);
        Long memberId = memberEntity != null ? memberEntity.getId() : null;
        Page<CommentEntity> parentComments = commentRepository.findParentCommentsByPostId(postId, CommentType.PARENT_COMMENT.getCode(), pageable);
        List<CommentEntity> childCommentEntities = commentRepository.findChildCommentsByPostId(postId);
        Map<Long, List<CommentEntity>> childCommentMap = childCommentEntities.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParent().getId()));
        Page<CommentResponse> commentResponses = mapCommentsToResponse(parentComments, memberId, childCommentMap);
        CommentMultiResponse response = new CommentMultiResponse();
        response.setPostId(postId);
        response.setPostTitle(post.getTitle());
        response.setBoardId(post.getBoardEntity().getId());
        response.setBoardName(post.getBoardEntity().getName());
        response.setComments(commentResponses.getContent());
        return new GlobalResponse<>(response, PageInfo.of(commentResponses));
    }

    private Page<CommentResponse> mapCommentsToResponse(Page<CommentEntity> parentComments, Long memberId, Map<Long, List<CommentEntity>> childCommentMap)
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

    public Page<CommentResponseForProfile> getAllCommentsForProfile(MemberEntity memberEntity, Pageable pageable)
    {
        return commentRepository.findCommentsForProfile(memberEntity.getId(), pageable);
    }

    public CommentSingleResponse getCommentSingleResponse(Long commentId, MemberEntity memberEntity)
    {
        CommentEntity commentEntity = getComment(commentId);
        boolean voteStatus = false;
        if (memberEntity != null)
            voteStatus = voteService.checkIfMemberVotedComment(memberEntity.getId(), commentEntity.getId());
        return CommentMapper.INSTANCE.toCommentSingleResponse(commentEntity, voteStatus);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public boolean deleteComment(Long commentId, MemberEntity currentMemberEntity)
    {
        CommentEntity commentEntity = getComment(commentId);
        PostEntity post = commentEntity.getPostEntity();
        authService.checkPermission(commentEntity.getMemberEntity().getId(), currentMemberEntity);
        if (commentEntity.getChildren().isEmpty()) {
            voteService.deleteAllVoteInTheComment(commentId);
            commentRepository.deleteById(commentId);
            post.decreaseCommentCount();
            return !commentRepository.existsById(commentId);
        } else {
            commentEntity.deleteComment();
            return true;
        }
    }

    @Transactional
    public void deleteAllByPostId(Long postId)
    {
        List<CommentEntity> commentEntityList = commentRepository.findAllByPostEntityId(postId);
        commentEntityList
                .forEach(comment -> {
                    voteService.deleteAllVoteInTheComment(comment.getId());
                });
        commentRepository.deleteAllInBatch(commentEntityList);
    }

    private void checkIfPostHasExactComment(PostEntity post, CommentEntity commentEntity)
    {
        if (commentEntity.getPostEntity() != post)
            throw new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND);
    }

}
