package com.cpayusin.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cpayusin.config.TestContainerExtension;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.global.security.jwt.JwtService;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.member.MemberRegisterRequest;
import com.cpayusin.repository.MemberRepository;
import com.cpayusin.repository.RedisRepository;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.service.impl.AuthenticationServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(TearDownExtension.class)
@ExtendWith(TestContainerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthenticationTest extends DummyObject
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private JwtService jwtService;

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp()
    {
        Member member1 = newMember("aa@naver.com", "test");

        memberRepository.save(member1);
        em.clear();
    }

    @Test
    void signUp_test() throws Exception
    {
        // given
        MemberRegisterRequest request = new MemberRegisterRequest();
        request.setEmail("bb@naver.com");
        request.setNickname("test2");
        request.setPassword("123456789");

        String requestBody = objectMapper.writeValueAsString(request);

        // when
        when(redisRepository.hasKey(any())).thenReturn(true);

        ResultActions resultActions = mvc
                .perform(post("/api/v1/sign-up")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body " + responseBody);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("test2"))
                .andExpect(jsonPath("$.data.email").value("bb@naver.com"))
                .andExpect(jsonPath("$.data.score").value("0"));
    }


    @WithMockUser(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void logout() throws Exception
    {
        // given
        String email = "aa@naver.com";
        String refreshToken = jwtService.generateRefreshToken(email);

        System.out.println("refresh token = " +refreshToken);


        when(redisRepository.hasKey(eq(refreshToken))).thenReturn(true);
        when(redisRepository.hasKey(refreshToken)).thenReturn(true);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/v1/logout")
                        .header("Refresh", refreshToken));

        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response = " + responseBody);
    }

    @Test
    void reissue()
    {
        // given

        // when

        // then
    }

    @Test
    void verifyCode()
    {
        // given

        // when

        // then
    }

    @Test
    void resetPassword()
    {
        // given

        // when

        // then
    }
}