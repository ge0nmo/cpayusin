package com.cpayusin.post.controller;

import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.post.controller.response.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@RestController
public class PostController
{
    private final PostService postService;

    @PostMapping(value = "/post/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse<PostCreateResponse>> savePost(@RequestPart(value = "data") @Valid PostCreateRequest request,
                                                                       @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                                                       @AuthenticationPrincipal MemberDetails currentMember)
    {
        var data = postService.createPost(request, files, currentMember.getMember());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PatchMapping("/post/update/{post-id}")
    public ResponseEntity<GlobalResponse<PostUpdateResponse>> updatePost(@RequestPart(value = "data") @Valid PostUpdateRequest request,
                                                                         @PathVariable("post-id") @Positive Long postId,
                                                                         @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                                                         @AuthenticationPrincipal MemberDetails currentMember)
    {
        var data = postService.updatePost(postId, request, files, currentMember.getMember());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/post/{post-id}")
    public ResponseEntity<GlobalResponse<PostSingleResponse>> getPost(@PathVariable("post-id") @Positive Long postId,
                                                                      @AuthenticationPrincipal MemberDetails currentMember)
    {
        Member member = currentMember != null ? currentMember.getMember() : null;
        var data = postService.getPostSingleResponse(postId, member);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/profile/my-posts")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse<List<PostResponseForProfile>>> getMyPosts(@AuthenticationPrincipal MemberDetails currentMember,
                                                                                   @PageableDefault Pageable pageable)
    {
        Page<PostResponseForProfile> data = postService.getMyPosts(currentMember.getMember(), pageable.previousOrFirst());

        return ResponseEntity.ok(new GlobalResponse<>(data.getContent(), PageInfo.of(data)));
    }

    @GetMapping("/post/board")
    public ResponseEntity<GlobalResponse<List<PostMultiResponse>>> getAllByBoardId(@PageableDefault Pageable pageable,
                                                                                   @RequestParam("id") Long boardId)
    {
        GlobalResponse<List<PostMultiResponse>> response = postService.getPostsByBoardId(boardId, pageable.previousOrFirst());

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/post/delete/{post-id}")
    public ResponseEntity<GlobalResponse<String>> deletePost(@PathVariable("post-id") @Positive Long postId,
                                     @AuthenticationPrincipal MemberDetails currentMember)
    {
        boolean result = postService.deletePostById(postId, currentMember.getMember());

        if(result)
            return ResponseEntity.ok(new GlobalResponse<>("삭제가 완료되었습니다."));

        else
            return ResponseEntity.ok(new GlobalResponse<>("삭제에 실패했습니다."));
    }

    @GetMapping("/post")
    public ResponseEntity<SliceDto<PostMultiResponse>> getAllPostsByBoardId(@RequestParam("boardId") long boardId,
                                                                              @RequestParam(value = "lastPost", required = false) Long lastPost,
                                                                              @PageableDefault Pageable pageable)
    {
        SliceDto<PostMultiResponse> response = postService.getPostByBoardId(boardId, lastPost, pageable);

        return ResponseEntity.ok(response);
    }
}
