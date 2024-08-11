package com.jbaacount.payload.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jbaacount.model.Board;
import com.jbaacount.model.Member;
import com.jbaacount.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestResponse
{
    private MemberResponse member;
    private BoardResponse board;
    private PostResponse post;

    public TestResponse(Post post)
    {
        this.member = new MemberResponse(post.getMember());
        this.board = new BoardResponse(post.getBoard());
        this.post = new PostResponse(post);
    }

    @Data
    @AllArgsConstructor
    static class MemberResponse
    {
        private long id;
        private String nickname;

        public MemberResponse(Member member)
        {
            this.id = member.getId();
            this.nickname = member.getNickname();
        }
    }

    @Data
    @AllArgsConstructor
    static class BoardResponse
    {
        private long id;
        private String name;

        public BoardResponse(Board board)
        {
            this.id = board.getId();
            this.name = board.getName();
        }
    }

    @Data
    @AllArgsConstructor
    static class PostResponse
    {
        private long id;
        private String title;
        private String content;
        private int voteCount;

        private long commentsCount;

        @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
        private LocalDateTime createdAt;

        public PostResponse(Post post)
        {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.voteCount = post.getVoteCount();
            this.commentsCount = post.getComments().size();
            this.createdAt = post.getCreatedAt();
        }

    }
}
