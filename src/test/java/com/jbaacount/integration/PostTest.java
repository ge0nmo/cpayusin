package com.jbaacount.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbaacount.config.TestContainerExtension;
import com.jbaacount.dummy.DummyObject;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.post.PostCreateRequest;
import com.jbaacount.repository.BoardRepository;
import com.jbaacount.repository.MemberRepository;
import com.jbaacount.repository.PostRepository;
import com.jbaacount.config.TearDownExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(TestContainerExtension.class)
@ExtendWith(TearDownExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PostTest extends DummyObject
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostRepository postRepository;

    private Member member;
    private Board board1;
    private Board board2;


    @BeforeEach
    void setUp()
    {
        member = newMockMember(1L, "aa@naver.com", "mockUser", "ADMIN");
        memberRepository.save(member);

        board1 = boardRepository.save(newMockBoard(1L, "board1", 1));

        board2 = boardRepository.save(newMockBoard(2L, "board2", 2));

        postRepository.save(newMockPost(1L, "title", "content", board1, member));
    }


    @WithUserDetails(value = "aa@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createPost_test() throws Exception
    {
        // given
        String title = "title";
        String content = "\"      #backgroundImage {\n" +
                "        border: none;\n" +
                "        height: 100%;\n" +
                "        pointer-events: none;\n" +
                "        position: fixed;\n" +
                "        top: 0;\n" +
                "        visibility: hidden;\n" +
                "        width: 100%;\n" +
                "      }\n" +
                "      [show-background-image] #backgroundImage {\n" +
                "        visibility: visible;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <iframe id=\"backgroundImage\" src=\"\"></iframe>\n" +
                "    <ntp-app></ntp-app>\n" +
                "    <script type=\"module\" src=\"new_tab_pasdafasdfsdf" +
                "sdfsdfsdf" +
                "sdfsdfsdfsdfsdfsdfge.js\"></script>\n" +
                "    <link rel=\"stylesheet\" href=\"chrome://resources/css/text_defaults_md.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"chrome://theme/colors.css?sets=ui,chrome\">\n" +
                "    <link rel=\"stylesheet\" href=\"shared_vars.css\">\n" +
                "  </body>\n" +
                "</html>\"";

        PostCreateRequest request = new PostCreateRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setBoardId(1L);

        String requestBody = objectMapper.writeValueAsString(request);

        MockMultipartFile jsonData = new MockMultipartFile(
                "data",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes());

        MemberDetails memberDetails = new MemberDetails(member);
        // when
        ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/post/create")
                .file(jsonData)
                        .with(user(memberDetails))
                .characterEncoding("UTF-8"));


        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));

        System.out.println("response body = " + resultActions.andReturn().getResponse().getContentAsString());
    }


}