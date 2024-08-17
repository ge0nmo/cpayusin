package com.cpayusin.controller;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.MemberController;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.request.EmailRequest;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.request.NicknameRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.domain.Member;
import com.cpayusin.member.service.MemberValidator;
import com.cpayusin.setup.RestDocsSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsSetup
{
    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberValidator memberValidator;

    @Test
    void updateMember() throws Exception
    {
        // given
        String nickname = "관리자 수정";

        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .nickname(nickname)
                .password("12341234123")
                .build();

        MemberUpdateResponse response = MemberUpdateResponse.builder()
                .id(1L)
                .email("admin@gmail.com")
                .nickname(nickname)
                .role("ADMIN")
                .url(URL)
                .build();

        byte[] requestBody = objectMapper.writeValueAsBytes(request);

        String fullPath = FILE_PATH1 + FILE_NAME1;

        MockMultipartFile image =
                new MockMultipartFile("image", FILE_NAME1, "image/jpeg", new FileInputStream(fullPath));

        MockMultipartFile data = new MockMultipartFile("data", null, MediaType.APPLICATION_JSON_VALUE, requestBody);

        given(memberService.updateMember(any(MemberUpdateRequest.class), any(MockMultipartFile.class), any(Member.class)))
                .willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(multipartPatchBuilder("/api/v1/member/update")
                        .file(data)
                        .file(image)
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(1L))
                .andExpect(jsonPath("data.nickname").value(nickname))
                .andExpect(jsonPath("data.url").value(URL))

                .andDo(document("member update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("data").description("Json 데이터"),
                                partWithName("image").description("파일").optional()
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.url").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 권한")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getMemberOwnProfile() throws Exception
    {
        // given
        MemberDetailResponse response = MemberDetailResponse.builder()
                .id(1L)
                .nickname("관리자")
                .email("admin@gmail.com")
                .role("ADMIN")
                .url(URL)
                .createdAt(LocalDateTime.now())
                .build();

        given(memberService.getMemberDetailResponse(any(Long.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/member/profile")
                        .with(user(memberDetails))
                        .with(csrf()));


        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(1L))
                .andExpect(jsonPath("data.nickname").value("관리자"))
                .andExpect(jsonPath("data.email").value("admin@gmail.com"))

                .andDo(document("member profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 권한"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("회원가입 날짜")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
    }


    @Test
    void getSingleMember() throws Exception
    {
        // given
        MemberSingleResponse response = MemberSingleResponse.builder()
                .id(2L)
                .nickname("홍길동")
                .role("USER")
                .profileImage(URL)
                .build();

        given(memberService.getMemberSingleResponse(any(Long.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/member/get/{member-id}", 2L)
                        .with(user(memberDetails))
                        .with(csrf()));


        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(2L))
                .andExpect(jsonPath("data.nickname").value("홍길동"))

                .andDo(document("member single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("member-id").description("유저 고유 식별 번호")
                        ),

                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 권한")
                        ).andWithPrefix("", pageNoContentResponseFields())
                ));
    }


    @Test
    void getMemberList() throws Exception
    {
        // given
        Long memberId = 2L;

        List<MemberMultiResponse> members = Arrays.asList(
                new MemberMultiResponse(1L, "john", "john@example.com", "https://example.com/john.jpg", "USER"),
                new MemberMultiResponse(2L, "jane", "jane@example.com", "https://example.com/jane.jpg", "USER")
        );

        Pageable pageable = PageRequest.of(1, 8);
        Slice<MemberMultiResponse> slice = new SliceImpl<>(members, pageable, true);
        SliceDto<MemberMultiResponse> response = new SliceDto<>(members, slice);

        given(memberService.getAllMembers(any(), any(), any())).willReturn(response);

        // when
        ResultActions resultActions =
                mvc.perform(get("/api/v1/member/multi-info")
                        .param("keyword", "")
                        .param("member", memberId.toString()));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].nickname").value("john"))
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.data[0].profileImage").value("https://example.com/john.jpg"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].nickname").value("jane"))
                .andExpect(jsonPath("$.data[1].email").value("jane@example.com"))
                .andExpect(jsonPath("$.data[1].profileImage").value("https://example.com/jane.jpg"))
                .andExpect(jsonPath("$.data[1].role").value("USER"))
                .andExpect(jsonPath("$.sliceInfo.size").value(8))
                .andExpect(jsonPath("$.sliceInfo.numberOfElements").value(2))
                .andExpect(jsonPath("$.sliceInfo.hasNext").value(true))
                .andDo(document("get-memberEntities",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").optional().description("검색 키워드"),
                                parameterWithName("member").optional().description("유저 고유 식별 아이디")
                        ),
                        responseFields(
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("유저 고유 식별 아이디"),
                                fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data[].profileImage").type(JsonFieldType.STRING).description("유저 프로필 사진").optional(),
                                fieldWithPath("data[].role").type(JsonFieldType.STRING).description("유저 권한"),

                                fieldWithPath("sliceInfo.size").type(JsonFieldType.NUMBER).description("페이지 당 데이터 개수"),
                                fieldWithPath("sliceInfo.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 데이터 개수"),
                                fieldWithPath("sliceInfo.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지의 데이터 존재 여부")
                        )
                ));
    }


    @Test
    void deleteMember() throws Exception
    {
        // given

        given(memberService.deleteById(any(Member.class))).willReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/api/v1/member/delete")
                        .with(csrf())
                        .with(user(memberDetails)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("삭제되었습니다."))
                .andDo(document("delete member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data").description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void checkExistEmail() throws Exception
    {
        // given
        EmailRequest request = new EmailRequest("admin@gmail.com");
        String response = "사용할 수 있는 이메일입니다.";
        String requestBody = objectMapper.writeValueAsString(request);

        given(memberValidator.checkExistEmail(any(String.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/member/verify-email")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(response))
                .andDo(document("verify-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                        ),

                        responseFields(
                                fieldWithPath("data").description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void checkExistNickname() throws Exception
    {
        // given
        NicknameRequest request = new NicknameRequest("홍길동");
        String response = "사용할 수 있는 닉네임입니다.";
        String requestBody = objectMapper.writeValueAsString(request);

        given(memberValidator.checkExistNickname(any(String.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/member/verify-nickname")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(response))
                .andDo(document("verify-nickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                        ),

                        responseFields(
                                fieldWithPath("data").description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("result: " + resultActions.andReturn().getResponse().getContentAsString());
    }
}