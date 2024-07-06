package com.jbaacount.controller;

import com.jbaacount.payload.request.member.MemberRegisterRequest;
import com.jbaacount.payload.request.member.ResetPasswordDto;
import com.jbaacount.payload.request.member.VerificationDto;
import com.jbaacount.payload.response.AuthenticationResponse;
import com.jbaacount.payload.response.member.MemberCreateResponse;
import com.jbaacount.payload.response.member.ResetPasswordResponse;
import com.jbaacount.service.AuthenticationService;
import com.jbaacount.service.MemberService;
import com.jbaacount.setup.RestDocsSetup;
import com.jbaacount.validator.MemberValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jbaacount.utils.DescriptionUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest extends RestDocsSetup
{
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberValidator memberValidator;

    @Test
    void logout() throws Exception
    {
        // given
        given(authenticationService.logout(any())).willReturn("로그아웃 되었습니다.");

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/logout")
                        .with(csrf())
                        .header("Refresh", TOKEN))
                .andExpect(status().isOk())
                .andDo(document("logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Refresh").description("리프레시 토큰")
                        ),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("응답")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        // then
        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());


    }

    @Test
    void signUp() throws Exception
    {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .email("test@naver.com")
                .nickname("test1")
                .password("123456789")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        MemberCreateResponse response = MemberCreateResponse.builder()
                .id(1L)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .score(0)
                .role("USER")
                .build();

        given(authenticationService.register(any(MemberRegisterRequest.class))).willReturn(response);

        // when

        ResultActions resultActions = mvc
                .perform(post("/api/v1/sign-up")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        System.out.println("request body " + requestBody);
        System.out.println("response body " + responseBody);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.score").value(0))
                .andDo(document("signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호")
                            ),
                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 등급"),
                                fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("점수")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));
    }


    @Test
    void reissue() throws Exception
    {
        // given
        AuthenticationResponse response = AuthenticationResponse.builder()
                .memberId(1L)
                .role("ADMIN")
                .email("test@naver.com")
                .accessToken(TOKEN)
                .refreshToken(TOKEN)
                .build();

        given(authenticationService.reissue(any(String.class), any(String.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/reissue")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN)
                        .header("Refresh", TOKEN));


        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("reissue", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰"),
                                headerWithName("Refresh").description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 등급"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void verifyCode() throws Exception
    {
        // given
        VerificationDto verificationDto = VerificationDto.builder()
                .email("test@gmail.com")
                .verificationCode(VERIFICATION_CODE)
                .build();

        String requestBody = objectMapper.writeValueAsString(verificationDto);

        given(authenticationService.verifyCode(any(VerificationDto.class))).willReturn("인증이 완료되었습니다.");

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/v1/verification")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("verify code", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("verificationCode").description("인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("결과")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void resetPassword() throws Exception
    {
        // given
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .email("test@naver.com")
                .password("123456789")
                .build();

        String requestBody = objectMapper.writeValueAsString(resetPasswordDto);

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .id(1L)
                .email(resetPasswordDto.getEmail())
                .nickname("가시제거연구소")
                .role("ADMIN")
                .build();


        given(authenticationService.resetPassword(any(ResetPasswordDto.class))).willReturn(response);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/api/v1/reset-password")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("reset password", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("password").description("새 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 고유 식별 번호"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 등급")

                        ).andWithPrefix("", pageNoContentResponseFields())
                ));

        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());
    }
}