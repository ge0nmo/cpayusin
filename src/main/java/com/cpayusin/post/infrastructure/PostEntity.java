package com.cpayusin.post.infrastructure;

import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "postEntity",
        indexes = {
                @Index(name = "idx_post_created", columnList = "created_at")
        }
)
@Setter
@Getter
@Entity
public class PostEntity extends BaseEntity
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
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity boardEntity;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int commentCount;

    @Version
    private Integer version;

    @Builder
    public PostEntity(String title, String content)
    {
        this.title = title;
        this.content = content;
        this.voteCount = 0;
        this.commentCount = 0;
    }

    public void addMember(MemberEntity memberEntity)
    {
        this.memberEntity = memberEntity;
    }

    public void addBoard(BoardEntity board)
    {
        this.boardEntity = board;
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

}
