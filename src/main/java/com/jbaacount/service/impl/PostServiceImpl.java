package com.jbaacount.service.impl;

import com.jbaacount.global.dto.PageInfo;
import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.mapper.PostMapper;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import com.jbaacount.model.type.BoardType;
import com.jbaacount.payload.request.post.PostCreateRequest;
import com.jbaacount.payload.request.post.PostUpdateRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.post.*;
import com.jbaacount.repository.PostRepository;
import com.jbaacount.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class PostServiceImpl implements PostService
{
    private final PostRepository postRepository;
    private final UtilService utilService;
    private final BoardService boardService;
    private final VoteService voteService;
    private final FileService fileService;
    private final CommentService commentService;


    public PostServiceImpl(PostRepository postRepository,
                           UtilService utilService,
                           @Lazy BoardService boardService,
                           VoteService voteService,
                           FileService fileService,
                           @Lazy CommentService commentService)
    {
        this.postRepository = postRepository;
        this.utilService = utilService;
        this.boardService = boardService;
        this.voteService = voteService;
        this.fileService = fileService;
        this.commentService = commentService;
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, List<MultipartFile> files, Member currentMember)
    {
        Post post = PostMapper.INSTANCE.toPostEntity(request);
        Board board = boardService.getBoardById(request.getBoardId());
        utilService.isUserAllowed(board.getIsAdminOnly(), currentMember);
        post.addMember(currentMember);
        post.addBoard(board);
        Post savedPost = postRepository.save(post);
        List<String> urls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            fileService.storeFiles(files, savedPost)
                    .forEach(file -> urls.add(file.getUrl()));
        }
        currentMember.getScoreByPost();
        return PostMapper.INSTANCE.toPostCreateResponse(savedPost, urls);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> files, Member currentMember)
    {
        Post post = findById(postId);
        //Only the owner of the post has the authority to update
        utilService.isTheSameUser(post.getMember().getId(), currentMember.getId());
        Optional.ofNullable(request.getBoardId())
                .ifPresent(newBoardId -> {
                    Board board = boardService.getBoardById(newBoardId);
                    post.addBoard(board);
                });
        PostMapper.INSTANCE.updatePostFromUpdateRequest(request, post);
        if (!request.getDeletedImg().isEmpty()) {
            fileService.deleteUploadedFiles(request.getDeletedImg());
        }
        if (files != null && !files.isEmpty()) {
            fileService.storeFiles(files, post);
        }
        return PostMapper.INSTANCE.toPostUpdateResponse(post);
    }


    public Post findById(Long id)
    {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.POST_NOT_FOUND));
    }

    public PostSingleResponse getPostSingleResponse(Long id, Member member)
    {
        Post post = findById(id);
        boolean voteStatus = false;
        if (member != null) {
            voteStatus = voteService.checkIfMemberVotedPost(member.getId(), id);
        }
        PostSingleResponse response = PostMapper.INSTANCE.toPostSingleResponse(post, voteStatus);
        response.setFiles(fileService.getFileUrlByPostId(id));
        return response;
    }

    public Page<PostResponseForProfile> getMyPosts(Member member, Pageable pageable)
    {
        return postRepository.findAllByMemberIdForProfile(member.getId(), pageable);
    }

    @Transactional
    public boolean deletePostById(Long postId, Member currentMember)
    {
        Post post = findById(postId);
        utilService.checkPermission(post.getMember().getId(), currentMember);
        deleteRelatedDataInPost(postId);
        postRepository.deleteById(postId);
        return postRepository.existsById(postId);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public void deleteAllPostsByBoardId(Long boardId)
    {
        List<Post> postList = postRepository.findAllByBoardId(boardId);
        postList.forEach(post -> {
            deleteRelatedDataInPost(post.getId());
        });
        postRepository.deleteAllInBatch(postList);
    }

    //@Cacheable(value = "posts", key = "#boardId + '_' + #keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public GlobalResponse<List<PostMultiResponse>> getPostsByBoardId(long boardId, String keyword, Pageable pageable)
    {
        if(StringUtils.hasLength(keyword))
            keyword = keyword.toLowerCase();

        List<Long> boardList = getSubBoardList(boardId);

        Page<PostResponseProjection> pageResponse = postRepository.findAllPostByBoardId(boardList, keyword, pageable);
        List<PostMultiResponse> response = PostMapper.INSTANCE.toPostMultiResponses(pageResponse.getContent());

        return new GlobalResponse<>(response, PageInfo.of(pageResponse));
    }

    private List<Long> getSubBoardList(long boardId)
    {
        List<Long> boardList = boardService.getBoardIdListByParentId(boardId);
        boardList.add(boardId);

        return boardList;
    }


    private void deleteRelatedDataInPost(Long postId)
    {
        fileService.deleteUploadedFiles(postId);
        voteService.deleteAllVoteInThePost(postId);
        commentService.deleteAllByPostId(postId);
    }
}