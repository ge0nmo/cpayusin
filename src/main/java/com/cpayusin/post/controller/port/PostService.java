package com.cpayusin.post.controller.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.controller.response.*;
import com.cpayusin.post.domain.Post;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.common.controller.response.GlobalResponse;
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
