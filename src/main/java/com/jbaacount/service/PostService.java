package com.jbaacount.service;

import com.jbaacount.global.dto.SliceDto;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import com.jbaacount.payload.request.post.PostCreateRequest;
import com.jbaacount.payload.request.post.PostUpdateRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.post.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService
{
    PostCreateResponse createPost(PostCreateRequest request, List<MultipartFile> files, Member currentMember);

    PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> files, Member currentMember);

    Post findById(Long id);

    PostSingleResponse getPostSingleResponse(Long id, Member member);

    Page<PostResponseForProfile> getMyPosts(Member member, Pageable pageable);

    boolean deletePostById(Long postId, Member currentMember);

    void deleteAllPostsByBoardId(Long boardId);

    GlobalResponse<List<PostMultiResponse>> getPostsByBoardId(long boardId, Pageable pageable);

    SliceDto<PostMultiResponse> getPostByBoardId(long boardId, Long lastPost, Pageable pageable);
}
