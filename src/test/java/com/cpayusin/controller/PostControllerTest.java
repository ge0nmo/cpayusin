package com.cpayusin.controller;

import com.cpayusin.common.controller.response.PageInfo;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.post.controller.PostController;
import com.cpayusin.post.controller.port.PostService;
import com.cpayusin.post.controller.response.*;
import com.cpayusin.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.cpayusin.utils.DescriptionUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest extends RestDocsSetup
{
    @MockBean
    private PostService postService;

    private static final String CONTENT = "로렘 입숨(lorem ipsum; 줄여서 립숨, lipsum)은 출판이나 그래픽 디자인 분야에서 폰트, 타이포그래피, 레이아웃 같은 그래픽 요소나 시각적 연출을 보여줄 때 사용하는 표준 채우기 텍스트로, 최종 결과물에 들어가는 실제적인 문장 내용이 채워지기 전에 시각 디자인 프로젝트 모형의 채움 글로도 이용된다. 이런 용도로 사용할 때 로렘 입숨을 그리킹(greeking)이라고도 부르며, 때로 로렘 입숨은 공간만 차지하는 무언가를 지칭하는 용어로도 사용된다.";

    @Test
    void savePost() throws Exception
    {
        // given
        Long boardId = 1L;
        Long postId = 1L;
        String title = "첫번째 게시판";

        PostCreateRequest request = PostCreateRequest.builder()
                .title(title)
                .boardId(boardId)
                .content(CONTENT)
                .build();

        PostCreateResponse response = PostCreateResponse.builder()
                .id(postId)
                .title(title)
                .content(CONTENT)
                .createdAt(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writeValueAsString(request);


        given(postService.createPost(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/post")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("create post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시판 식별 번호")
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("게시글 생성 날짜")
                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updatePost() throws Exception
    {
        // given
        Long postId = 1L;
        String title = "고등어 가시 팁";
        String content = "안드시면 됩니다.";

        PostUpdateResponse response = PostUpdateResponse.builder()
                .id(postId)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        PostUpdateRequest request = PostUpdateRequest.builder()
                .title(title)
                .content(content)
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        given(postService.updatePost(any(Long.class), any(PostUpdateRequest.class), any())).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/api/v1/post/" + postId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("update post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("게시글 생성 날짜")
                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getPost() throws Exception
    {
        // given
        Long boardId = 1L;
        String boardName = "게시판 제목";
        Long memberId = 1L;
        Long postId = 3L;
        String nickname = "관리자";
        String title = "게시글 제목";
        String content = "게시글 내용";

        PostSingleResponse response = PostSingleResponse.builder()
                .boardId(boardId)
                .memberId(memberId)
                .boardName(boardName)
                .nickname(nickname)
                .id(postId)
                .title(title)
                .content(content)
                .voteCount(1)
                .voteStatus(true)
                .createdAt(LocalDateTime.now())
                .build();

        given(postService.getPostSingleResponse(any(Long.class), any(Member.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(RestDocumentationRequestBuilders.get("/api/v1/post/{post-id}", postId)
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.memberId").value(memberId))
                .andExpect(jsonPath("data.boardId").value(boardId))
                .andExpect(jsonPath("data.nickname").value(nickname))
                .andExpect(jsonPath("data.title").value(title))
                .andExpect(jsonPath("data.content").value(content))
                .andExpect(jsonPath("data.voteStatus").value(true))
                .andExpect(jsonPath("data.voteCount").value(1))
                .andDo(document("post-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("post-id").description("게시글 고유 식별 번호")
                        ),

                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 고유 식별 번호"),
                                fieldWithPath("data.boardName").type(JsonFieldType.STRING).description("게시판 이름"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),

                                fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("data.voteCount").type(JsonFieldType.NUMBER).description("게시글 숫자"),
                                fieldWithPath("data.voteStatus").type(JsonFieldType.BOOLEAN).description("게시글 투표 여부"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("게시글 생성 날짜")
                        ).andWithPrefix("", pageNoContentResponseFields())
                ))
        ;

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());

    }

    @Test
    void getMyPosts() throws Exception
    {
        // given
        PostResponseForProfile response1 = PostResponseForProfile.builder()
                .boardId(1L)
                .boardName("게시판1")
                .title("고등어 구이 vs 조림")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        PostResponseForProfile response2 = PostResponseForProfile.builder()
                .boardId(1L)
                .boardName("게시판1")
                .title("감자 튀김 vs 고구마 튀김")
                .postId(2L)
                .createdAt(LocalDateTime.now())
                .build();

        PostResponseForProfile response3 = PostResponseForProfile.builder()
                .boardId(1L)
                .boardName("게시판1")
                .title("Don't cry 박봄 vs 더크로스")
                .postId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        PostResponseForProfile response4 = PostResponseForProfile.builder()
                .boardId(1L)
                .boardName("게시판1")
                .title("농심 육개장 vs 삼양 육개장")
                .postId(4L)
                .createdAt(LocalDateTime.now())
                .build();

        List<PostResponseForProfile> responseList = List.of(response1, response2, response3, response4);

        Integer page = 1;
        Integer size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseForProfile> pageResponse = new PageImpl<>(responseList, pageable, responseList.size());

        given(postService.getMyPosts(any(Member.class), any(Pageable.class))).willReturn(pageResponse);

        // when
        ResultActions resultActions = mvc
                .perform(RestDocumentationRequestBuilders.get("/api/v1/profile/my-posts")
                        .with(user(memberDetails))
                        .param("page", page.toString())
                        .param("size", size.toString()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("profile-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 당 데이터 개수")
                        ),

                        responseFields(
                                fieldWithPath("data[].boardId").type(JsonFieldType.NUMBER).description("게시판 고유 식별 번호"),
                                fieldWithPath("data[].boardName").type(JsonFieldType.STRING).description("게시판 제목"),
                                fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("게시글 생성 날짜"),

                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지 숫자"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지당 데이터 개수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("데이터 총 개수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("페이지 총 개수"),
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description(SUCCESS),
                                fieldWithPath("message").type(JsonFieldType.STRING).description(MESSAGE),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description(CODE),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(STATUS)
                        )
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllByBoardId() throws Exception
    {
        // given
        Long boardId = 1L;
        String boardName = "공지사항 게시판";
        Long memberId = 1L;
        String memberName = "관리자";

        PostMultiResponse response1 = PostMultiResponse.builder()
                .boardId(boardId)
                .boardName(boardName)
                .memberId(memberId)
                .memberName(memberName)
                .postId(1L)
                .title("1월 21일 업데이트 공지")
                .voteCount(1)
                .commentsCount(2)
                .createdAt(LocalDateTime.now())
                .build();

        PostMultiResponse response2 = PostMultiResponse.builder()
                .boardId(boardId)
                .boardName(boardName)
                .memberId(memberId)
                .memberName(memberName)
                .title("3월 3일 업데이트 공지")
                .postId(2L)
                .voteCount(3)
                .commentsCount(2)
                .createdAt(LocalDateTime.now())
                .build();

        PostMultiResponse response3 = PostMultiResponse.builder()
                .boardId(boardId)
                .boardName(boardName)
                .memberId(memberId)
                .memberName(memberName)
                .title("5월 2일 업데이트 공지")
                .postId(3L)
                .voteCount(3)
                .commentsCount(2)
                .createdAt(LocalDateTime.now())
                .build();

        List<PostMultiResponse> responseList = List.of(response1, response2, response3);

        Integer page = 1;
        Integer size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<PostMultiResponse> pageResponse = new PageImpl<>(responseList, pageable, responseList.size());

        GlobalResponse<List<PostMultiResponse>> response = new GlobalResponse<>(responseList, PageInfo.of(pageResponse));


        given(postService.getPostsByBoardId(any(Long.class), any(Pageable.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(RestDocumentationRequestBuilders.get("/api/v1/post/board")
                        .param("id", boardId.toString())
                        .param("page", page.toString())
                        .param("size", size.toString()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("get-board-posts",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("id").description("게시판 고유 식별 번호"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 당 데이터 개수")
                        ),

                        responseFields(
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data[].memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("data[].boardId").type(JsonFieldType.NUMBER).description("게시판 고유 식별 번호"),
                                fieldWithPath("data[].boardName").type(JsonFieldType.STRING).description("게시판 이름"),
                                fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 고유 식별 번호"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data[].voteCount").type(JsonFieldType.NUMBER).description("추천 개수"),
                                fieldWithPath("data[].commentsCount").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("게시글 생성 날짜")

                        ).andWithPrefix("", pageInfoResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void deletePost() throws Exception
    {
        // given
        Long postId = 1L;

        given(postService.deletePostById(any(Long.class), any(Member.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(RestDocumentationRequestBuilders.delete("/api/v1/post/delete/{post-id}", postId)
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value("삭제가 완료되었습니다."))
                .andDo(document("delete-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("post-id").description("게시글 고유 식별 번호")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("데이터")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response = " + resultActions.andReturn().getResponse().getContentAsString());
    }
}