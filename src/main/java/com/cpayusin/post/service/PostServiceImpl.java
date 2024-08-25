package com.cpayusin.post.service;

import com.cpayusin.board.controller.port.BoardService;
import com.cpayusin.board.domain.Board;
import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.common.service.UtilService;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.post.mapper.PostMapper;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.post.controller.response.*;
import com.cpayusin.post.domain.Post;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.controller.port.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

        return PostMapper.INSTANCE.toPostCreateResponse(savedPost, urls);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> files, Member currentMember)
    {
        Post post = findById(postId);
        //Only the owner of the postEntity has the authority to update
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

    @Override
    public Post findByIdWithOptimisticLock(Long id)
    {
        return postRepository.findByIdWithOptimisticLock(id)
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

        log.info("created at in post = {}", post.getCreatedAt());
        log.info("created at in response = {}", response.getCreatedAt());

        return response;
    }

    public Page<PostResponseForProfile> getMyPosts(Member member, Pageable pageable)
    {
        return postRepository.findAllByMemberIdForProfile(member.getId(), pageable);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public boolean deletePostById(Long postId, Member currentMember)
    {
        Post post = findById(postId);
        utilService.checkPermission(post.getMember().getId(), currentMember);
        deleteRelatedDataInPost(postId);
        postRepository.deleteById(postId);
        return !postRepository.existsById(postId);
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

    @Cacheable(value = "posts", key = "#boardId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public GlobalResponse<List<PostMultiResponse>> getPostsByBoardId(long boardId, Pageable pageable)
    {
        List<Long> boardList = getSubBoardList(boardId);

        Page<PostResponseProjection> pageResponse = postRepository.findAllPostByBoardId(boardList, pageable);
        List<PostMultiResponse> response = PostMapper.INSTANCE.toPostMultiResponses(pageResponse.getContent());

        return new GlobalResponse<>(response, PageInfo.of(pageResponse));
    }

    @Override
    public SliceDto<PostMultiResponse> getPostByBoardId(Long boardId, Long lastPost, Pageable pageable)
    {
        List<Long> boardList = getSubBoardList(boardId);

        log.info("boardList = {}", boardList);

        Slice<PostMultiResponse> sliceResponse = postRepository.findAllPostsByBoardIds(boardList, lastPost, pageable);

        return new SliceDto<>(sliceResponse.getContent(), sliceResponse);
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
