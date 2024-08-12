package com.jbaacount.model;

import com.jbaacount.global.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "POST",
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

}
