package com.cpayusin.service;

import com.cpayusin.global.dto.SliceDto;
import com.cpayusin.model.Member;
import com.cpayusin.model.Post;
import com.cpayusin.payload.request.post.PostCreateRequest;
import com.cpayusin.payload.request.post.PostUpdateRequest;
import com.cpayusin.payload.response.GlobalResponse;
import com.cpayusin.payload.response.post.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService
{
    PostCreateResponse createPost(PostCreateRequest request, List<MultipartFile> files, Member currentMember);

    PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> files, Member currentMember);

    Post findById(Long id);

    Post findByIdWithOptimisticLock(Long id);

    PostSingleResponse getPostSingleResponse(Long id, Member member);

    Page<PostResponseForProfile> getMyPosts(Member member, Pageable pageable);

    boolean deletePostById(Long postId, Member currentMember);

    void deleteAllPostsByBoardId(Long boardId);

    GlobalResponse<List<PostMultiResponse>> getPostsByBoardId(long boardId, Pageable pageable);

    SliceDto<PostMultiResponse> getPostByBoardId(Long boardId, Long lastPost, Pageable pageable);
}
