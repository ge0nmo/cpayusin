package com.cpayusin.integration;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.config.TestContainerExtension;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.domain.Member;
import com.cpayusin.member.service.port.MemberRepository;
import com.cpayusin.setup.MockSetup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(TestContainerExtension.class)
@ExtendWith(TearDownExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MemberTest extends MockSetup
{
    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private ObjectMapper om;


    @BeforeEach
    void setUp()
    {
        mockMember = newMockMember(1L, "test@gmail.com", "test", "ADMIN");
        memberRepository.save(mockMember);

    }

    @WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateMember() throws Exception
    {
        // given
        MemberUpdateRequest request = new MemberUpdateRequest();
        request.setNickname("update");
        request.setUrl("https://jbact.s3.ap-northeast-2.amazonaws.com/post/99198ebb--91e2-61d2ea7febde.jpg");

        MemberDetails memberDetails = new MemberDetails(mockMember);
        String requestBody = om.writeValueAsString(request);

        // when
        ResultActions resultActions = mvc
                .perform(
                        patch("/api/v1/member/update")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(memberDetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.nickname").value("update"))
                .andExpect(jsonPath("data.url").value("https://jbact.s3.ap-northeast-2.amazonaws.com/post/99198ebb--91e2-61d2ea7febde.jpg"))
                ;

        // then
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

    }

    @Test
    void getMemberList() throws Exception
    {
        // given
        Member member3 = newMockMember(3L, "test3@gmail.com", "Ronaldo", "USER");
        Member member4 = newMockMember(4L, "test4@gmail.com", "Messy", "USER");
        Member member5 = newMockMember(5L, "test5@gmail.com", "McGregor", "USER");
        Member member6 = newMockMember(6L, "test6@gmail.com", "test6", "USER");

        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);

        int page = 1;
        int size = 3;

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/member/multi-info")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data[0].nickname").value(member6.getNickname()))
                .andExpect(jsonPath("data[1].nickname").value(member5.getNickname()))
                .andExpect(jsonPath("data[2].nickname").value(member4.getNickname()));


        // then

        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

    }

    private MockMultipartHttpServletRequestBuilder multipartPatchBuilder(String url)
    {
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(url);

        builder
                .with(request -> {
                    request.setMethod(HttpMethod.PATCH.name());
                    return request;
                });

        return builder;
    }



}