package com.jbaacount.setup;


import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbaacount.dummy.DummyObject;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.jbaacount.utils.DescriptionUtils.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@WithMockUser(roles = "ADMIN")
@AutoConfigureRestDocs
public abstract class RestDocsSetup extends DummyObject
{
    @MockBean
    protected RedisTemplate<String, String> redisTemplate;

    @MockBean
    protected ValueOperations<String, String> valueOperations;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mvc;

    protected MemberDetails memberDetails;
    protected Member member;
    protected  String URL = "https://jbaccount.s3.ap-northeast-2.amazonaws.com/post/1fe55e99-4bf3-4691-972f-2c67f75736fb.PNG";
    protected static final String FILE_PATH1= "src/test/resources/image/";
    protected static final String FILE_NAME1 = "photo1.jpeg";

    @BeforeEach
    void setUp()
    {
        member = newMockMember(1L, "test@gmail.com", "관리자", "ADMIN");
        memberDetails = new MemberDetails(member);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    protected FieldDescriptor[] pageNoContentResponseFields()
    {
        return new FieldDescriptor[]{
                fieldWithPath("pageInfo").type(JsonFieldType.NUMBER).description(PAGE_INFO).optional(),

                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description(SUCCESS),
                fieldWithPath("message").type(JsonFieldType.STRING).description(MESSAGE),
                fieldWithPath("code").type(JsonFieldType.NUMBER).description(CODE),
                fieldWithPath("status").type(JsonFieldType.STRING).description(STATUS)
        };
    }

    protected FieldDescriptor[] pageInfoResponseFields()
    {
        return new FieldDescriptor[]{
                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지 숫자"),
                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지당 데이터 개수"),
                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("데이터 총 개수"),
                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("페이지 총 개수"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description(SUCCESS),
                fieldWithPath("message").type(JsonFieldType.STRING).description(MESSAGE),
                fieldWithPath("code").type(JsonFieldType.NUMBER).description(CODE),
                fieldWithPath("status").type(JsonFieldType.STRING).description(STATUS)
        };
    }

    protected MockMultipartHttpServletRequestBuilder multipartPatchBuilder(String url)
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
