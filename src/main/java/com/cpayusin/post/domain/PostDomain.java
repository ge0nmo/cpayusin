package com.cpayusin.post.domain;

import com.cpayusin.board.domain.BoardDomain;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.post.controller.request.PostCreateRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Builder
@Getter
public class PostDomain
{
    private Long id;
    private String title;
    private String content;
    private int voteCount;
    private int commentCount;
    private MemberDomain memberDomain;
    private BoardDomain boardDomain;

    public static PostDomain from(PostCreateRequest request, MemberDomain memberDomain, BoardDomain boardDomain)
    {
       return PostDomain.builder()
               .title(request.getTitle())
               .content(StringUtils.hasLength(request.getContent())
                       ? request.getContent() : null)
               .voteCount(0)
               .commentCount(0)
               .memberDomain(memberDomain)
               .boardDomain(boardDomain)
               .build();
    }


}
