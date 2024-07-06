package com.jbaacount.controller;

import com.jbaacount.global.dto.PageInfo;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.post.PostCreateRequest;
import com.jbaacount.payload.request.post.PostUpdateRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.post.*;
import com.jbaacount.service.PostService;
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
                                                                                   @RequestParam(required = false) String keyword,
                                                                                   @RequestParam("id") Long boardId)
    {
        Page<PostMultiResponse> data = postService.getPostsByBoardId(boardId, keyword, pageable.previousOrFirst());

        return ResponseEntity.ok(new GlobalResponse<>(data.getContent(), PageInfo.of(data)));
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
}
