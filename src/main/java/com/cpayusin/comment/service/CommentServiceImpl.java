package com.cpayusin.comment.service;

import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.controller.request.CommentUpdateRequest;
import com.cpayusin.comment.controller.response.*;
import com.cpayusin.comment.domain.type.CommentType;
import com.cpayusin.comment.domain.Comment;
import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.common.service.UtilService;
import com.cpayusin.comment.mapper.CommentMapper;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.post.domain.Post;
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

import static java.util.stream.Collectors.toList;

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
        Page<Comment> parentCommentsPage = commentRepository.findParentCommentsByPostId(postId, CommentType.PARENT_COMMENT.getCode(), pageable);
        List<Comment> childCommentEntities = commentRepository.findChildCommentsByPostId(postId);

        Map<Long, List<CommentChildrenResponse>> childrenCommentMap = mapToChildResponse(memberId, childCommentEntities);

        List<CommentResponse> commentResponses = mapToCommentResponse(memberId, parentCommentsPage.getContent(), childrenCommentMap);

        return mapToGlobalResponse(post, commentResponses, parentCommentsPage);
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

    private GlobalResponse<CommentMultiResponse> mapToGlobalResponse(Post post, List<CommentResponse> commentResponses, Page<Comment> parentCommentsPage)
    {
        CommentMultiResponse response = new CommentMultiResponse();
        response.setPostId(post.getId());
        response.setPostTitle(post.getTitle());
        response.setBoardId(post.getBoard().getId());
        response.setBoardName(post.getBoard().getName());
        response.setComments(commentResponses);

        return new GlobalResponse<>(response, PageInfo.of(parentCommentsPage));
    }

    private List<CommentResponse> mapToCommentResponse(Long memberId, List<Comment> comments, Map<Long, List<CommentChildrenResponse>> commentChildrenMap)
    {
        return comments.stream()
                .map(comment -> {
                    CommentResponse response = CommentResponse.from(comment, voteService.checkIfMemberVotedComment(memberId, comment.getId()));
                    response.setChildren(commentChildrenMap.getOrDefault(comment.getId(), new ArrayList<>()));
                    return response;
                })
                .toList();
    }

    private Map<Long, List<CommentChildrenResponse>> mapToChildResponse(Long memberId, List<Comment> childComments)
    {
        return childComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getParent().getId(),
                        Collectors.mapping(
                                comment -> CommentChildrenResponse.from(comment, voteService.checkIfMemberVotedComment(memberId, comment.getId())),
                                toList()
                        )
                ));
    }
}