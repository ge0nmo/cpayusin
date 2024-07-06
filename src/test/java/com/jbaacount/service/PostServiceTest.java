package com.jbaacount.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbaacount.dummy.DummyObject;
import com.jbaacount.mapper.PostMapper;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import com.jbaacount.payload.request.post.PostCreateRequest;
import com.jbaacount.payload.request.post.PostUpdateRequest;
import com.jbaacount.payload.response.post.PostCreateResponse;
import com.jbaacount.payload.response.post.PostUpdateResponse;
import com.jbaacount.repository.BoardRepository;
import com.jbaacount.repository.MemberRepository;
import com.jbaacount.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest extends DummyObject
{
    @InjectMocks
    private PostService postService;

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

    private Member mockMember;
    private Board mockBoard;
    private Post mockPost;


    @BeforeEach
    void setUp()
    {
        mockMember = newMockMember(1L, "test@gmail.com", "운영자", "ADMIN");
        mockBoard = newMockBoard(1L, "board", 1);
        mockPost = newMockPost(1L, "title", "content", mockBoard, mockMember);
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

        Post postEntity = PostMapper.INSTANCE.toPostEntity(request);

        // stub 1
        given(boardService.getBoardById(any())).willReturn(mockBoard);

        // stub 2
        utilService.isUserAllowed(mockBoard.getIsAdminOnly(), mockMember);

        // stub 3
        given(postRepository.save(any())).willReturn(postEntity);

        // stub 4
        postEntity.addMember(mockMember);

        // stub 5
        postEntity.addBoard(mockBoard);

        // when
        PostCreateResponse response = postService.createPost(request, null, mockMember);
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
        //given(postService.getPostById(anyLong())).willReturn(mockPost);

        utilService.isUserAllowed(mockBoard.getIsAdminOnly(), mockMember);

        // when
        PostUpdateResponse response = postService.updatePost(1L, request, null, mockMember);

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
        utilService.checkPermission(mockPost.getMember().getId(), mockMember);

        // when
        postService.deletePostById(mockPost.getId(), mockMember);

        // then
        verify(postRepository, times(1)).findById(any());
    }
}