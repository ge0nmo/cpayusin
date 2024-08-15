package com.cpayusin.setup;

import com.cpayusin.board.service.port.BoardRepository;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import com.cpayusin.comment.service.port.CommentRepository;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.member.service.port.MemberRepository;
import com.cpayusin.post.service.port.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(TearDownExtension.class)
@SpringBootTest
public class FacadeSetUp extends MockSetup
{
    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected BoardRepository boardRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected CommentRepository commentRepository;

    protected ExecutorService es;
    protected CountDownLatch latch;

    protected MemberEntity memberEntity1;
    protected MemberEntity memberEntity2;
    protected MemberEntity memberEntity3;
    protected MemberEntity memberEntity4;
    protected MemberEntity memberEntity5;
    protected MemberEntity memberEntity6;
    protected MemberEntity memberEntity7;
    protected MemberEntity memberEntity8;
    protected MemberEntity memberEntity9;
    protected MemberEntity memberEntity10;
    protected List<MemberEntity> memberEntities;
    protected CommentCreateRequest request;

    @BeforeEach
    void threadSetUp()
    {
        memberEntity1 = newMockMember(1L, "memberEntity1@gmail.com", "memberEntity1", "USER");
        memberEntity2 = newMockMember(2L, "memberEntity2@gmail.com", "memberEntity2", "USER");
        memberEntity3 = newMockMember(3L, "memberEntity3@gmail.com", "memberEntity3", "USER");
        memberEntity4 = newMockMember(4L, "memberEntity4@gmail.com", "memberEntity4", "USER");
        memberEntity5 = newMockMember(5L, "memberEntity5@gmail.com", "memberEntity5", "USER");
        memberEntity6 = newMockMember(6L, "memberEntity6@gmail.com", "memberEntity6", "USER");
        memberEntity7 = newMockMember(7L, "memberEntity7@gmail.com", "memberEntity7", "USER");
        memberEntity8 = newMockMember(8L, "memberEntity8@gmail.com", "memberEntity8", "USER");
        memberEntity9 = newMockMember(9L, "memberEntity9@gmail.com", "memberEntity9", "USER");
        memberEntity10 = newMockMember(10L, "memberEntity10@gmail.com", "memberEntity10", "USER");

        List<MemberEntity> mockMemberEntities = List.of(memberEntity1, memberEntity2, memberEntity3, memberEntity4, memberEntity5, memberEntity6, memberEntity7, memberEntity8, memberEntity9, memberEntity10);
        memberEntities = memberRepository.saveAll(mockMemberEntities);
        mockBoard1 = boardRepository.save(mockBoard1);
        mockPost = postRepository.save(mockPost);

        mockCommentEntity = commentRepository.save(mockCommentEntity);
        es = Executors.newFixedThreadPool(4);
        latch = new CountDownLatch(memberEntities.size());

        request = CommentCreateRequest.builder()
                .text("comment")
                .postId(mockPost.getId())
                .build();
    }
}
