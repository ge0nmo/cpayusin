package com.cpayusin.post.infrastructure;

import com.cpayusin.board.infrastructure.Board;
import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.post.domain.PostDomain;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_post_created", columnList = "created_at")
        }
)
@Setter
@Getter
@Entity
public class Post extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int commentCount;

    @Version
    private Integer version;

    @Builder
    public Post(String title, String content)
    {
        this.title = title;
        this.content = content;
        this.voteCount = 0;
        this.commentCount = 0;
    }

    public void addMember(Member member)
    {
        this.member = member;
    }

    public void addBoard(Board board)
    {
        this.board = board;
    }

    public void updateTitle(String title)
    {
        this.title = title;
    }

    public void increaseCommentCount()
    {
        this.commentCount++;
    }

    public void decreaseCommentCount()
    {
        this.commentCount--;
    }

    public void updateContent(String content)
    {
        this.content = content;
    }

    public void upVote()
    {
        this.voteCount++;
    }

    public void downVote()
    {
        this.voteCount--;
    }

    public static Post from(PostDomain domain)
    {
        Post post = new Post();
        post.id = domain.getId();
        post.title = domain.getTitle();
        post.content = domain.getContent();
        post.voteCount = domain.getVoteCount();
        post.commentCount = domain.getCommentCount();
        post.board = Board.from(domain.getBoardDomain());
        post.member = Member.from(domain.getMemberDomain());

        return post;
    }


    public PostDomain toModel()
    {
        return PostDomain.builder()
                .id(id)
                .title(title)
                .content(content)
                .voteCount(voteCount)
                .commentCount(commentCount)
                .boardDomain(board.toModel())
                .build();
    }

}
