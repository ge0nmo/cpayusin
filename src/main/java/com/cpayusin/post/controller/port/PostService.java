package com.cpayusin.post.controller.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.controller.response.*;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.common.controller.response.GlobalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService
{
    PostCreateResponse createPost(PostCreateRequest request, List<MultipartFile> files, MemberEntity currentMemberEntity);

    PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> files, MemberEntity currentMemberEntity);

    PostEntity findById(Long id);

    PostEntity findByIdWithOptimisticLock(Long id);

    PostSingleResponse getPostSingleResponse(Long id, MemberEntity memberEntity);

    Page<PostResponseForProfile> getMyPosts(MemberEntity memberEntity, Pageable pageable);

    boolean deletePostById(Long postId, MemberEntity currentMemberEntity);

    void deleteAllPostsByBoardId(Long boardId);

    GlobalResponse<List<PostMultiResponse>> getPostsByBoardId(long boardId, Pageable pageable);

    SliceDto<PostMultiResponse> getPostByBoardId(Long boardId, Long lastPost, Pageable pageable);
}
