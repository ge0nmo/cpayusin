package com.cpayusin.setup;

import com.cpayusin.config.TearDownExtension;
import com.cpayusin.facade.VoteFacade;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.comment.CommentCreateRequest;
import com.cpayusin.repository.BoardRepository;
import com.cpayusin.repository.CommentRepository;
import com.cpayusin.repository.MemberRepository;
import com.cpayusin.repository.PostRepository;
import com.cpayusin.service.VoteService;
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

    protected Member member1;
    protected Member member2;
    protected Member member3;
    protected Member member4;
    protected Member member5;
    protected Member member6;
    protected Member member7;
    protected Member member8;
    protected Member member9;
    protected Member member10;
    protected List<Member> members;
    protected CommentCreateRequest request;

    @BeforeEach
    void threadSetUp()
    {
        member1 = newMockMember(1L, "member1@gmail.com", "member1", "USER");
        member2 = newMockMember(2L, "member2@gmail.com", "member2", "USER");
        member3 = newMockMember(3L, "member3@gmail.com", "member3", "USER");
        member4 = newMockMember(4L, "member4@gmail.com", "member4", "USER");
        member5 = newMockMember(5L, "member5@gmail.com", "member5", "USER");
        member6 = newMockMember(6L, "member6@gmail.com", "member6", "USER");
        member7 = newMockMember(7L, "member7@gmail.com", "member7", "USER");
        member8 = newMockMember(8L, "member8@gmail.com", "member8", "USER");
        member9 = newMockMember(9L, "member9@gmail.com", "member9", "USER");
        member10 = newMockMember(10L, "member10@gmail.com", "member10", "USER");

        List<Member> mockMembers = List.of(member1, member2, member3, member4, member5, member6, member7, member8, member9, member10);
        members = memberRepository.saveAll(mockMembers);
        mockBoard1 = boardRepository.save(mockBoard1);
        mockPost = postRepository.save(mockPost);

        mockComment = commentRepository.save(mockComment);
        es = Executors.newFixedThreadPool(4);
        latch = new CountDownLatch(members.size());

        request = CommentCreateRequest.builder()
                .text("comment")
                .postId(mockPost.getId())
                .build();
    }
}
