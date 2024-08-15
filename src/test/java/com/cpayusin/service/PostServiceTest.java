package com.cpayusin.service;

import com.cpayusin.board.controller.port.BoardService;
import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.comment.controller.port.CommentService;
import com.cpayusin.common.service.UtilService;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.mapper.PostMapper;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.member.service.port.MemberRepository;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.post.controller.response.PostCreateResponse;
import com.cpayusin.post.controller.response.PostUpdateResponse;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.post.service.PostServiceImpl;
import com.cpayusin.post.service.port.PostRepository;
import com.cpayusin.vote.controller.port.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest extends DummyObject
{
    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FileService fileService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private UtilService utilService;

    @Mock
    private VoteService voteService;

    @Mock
    private CommentService commentService;

    @Spy
    private ObjectMapper om;

    private MemberEntity mockMemberEntity;
    private BoardEntity mockBoard;
    private PostEntity mockPost;


    @BeforeEach
    void setUp()
    {
        mockMemberEntity = newMockMember(1L, "test@gmail.com", "운영자", "ADMIN");
        mockBoard = newMockBoard(1L, "boardEntity", 1);
        mockPost = newMockPost(1L, "title", "content", mockBoard, mockMemberEntity);
    }

    @Test
    void createPost() throws Exception
    {
        // given
        PostCreateRequest request = new PostCreateRequest();
        request.setBoardId(1L);
        request.setTitle("게시글1");
        request.setContent("게시글 내용");
        request.setBoardId(1L);

        PostEntity postEntity = PostMapper.INSTANCE.toPostEntity(request);

        // stub 1
        given(boardService.getBoardById(any())).willReturn(mockBoard);

        // stub 2
        utilService.isUserAllowed(mockBoard.getIsAdminOnly(), mockMemberEntity);

        // stub 3
        given(postRepository.save(any())).willReturn(postEntity);

        // stub 4
        postEntity.addMember(mockMemberEntity);

        // stub 5
        postEntity.addBoard(mockBoard);

        // when
        PostCreateResponse response = postService.createPost(request, null, mockMemberEntity);
        String responseBody = om.writeValueAsString(response);

        // then
        assertThat(response.getContent()).isEqualTo("게시글 내용");
        System.out.println("response body " + responseBody);

    }

    @Test
    void updatePost() throws Exception
    {
        // given
        String updateTitle = "update title";
        String updateContent = "update content";
        PostUpdateRequest request = new PostUpdateRequest();
        request.setContent(updateContent);
        request.setTitle(updateTitle);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
        //given(postService.findById(anyLong())).willReturn(mockPost);

        utilService.isUserAllowed(mockBoard.getIsAdminOnly(), mockMemberEntity);

        // when
        PostUpdateResponse response = postService.updatePost(1L, request, null, mockMemberEntity);

        // then
        assertEquals(updateContent, response.getContent());
        assertEquals(updateTitle, response.getTitle());

        verify(postRepository, times(1)).findById(any());

        System.out.println("response = " + om.writeValueAsString(response));
    }

    @Test
    void deletePostById()
    {
        // given
        given(postRepository.findById(any())).willReturn(Optional.of(mockPost));
        utilService.checkPermission(mockPost.getMemberEntity().getId(), mockMemberEntity);

        // when
        postService.deletePostById(mockPost.getId(), mockMemberEntity);

        // then
        verify(postRepository, times(1)).findById(any());
    }
}