package com.cpayusin.vote.infrastructure;

import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote")
@Entity
public class VoteEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static VoteEntity votePost(MemberEntity memberEntity, PostEntity post)
    {
        return new VoteEntity(memberEntity, post);
    }

    public static VoteEntity voteComment(MemberEntity memberEntity, CommentEntity commentEntity)
    {
        return new VoteEntity(memberEntity, commentEntity);
    }


    public VoteEntity(MemberEntity memberEntity, PostEntity postEntity)
    {
        this.memberEntity = memberEntity;
        this.postEntity = postEntity;
    }

    public VoteEntity(MemberEntity memberEntity, CommentEntity commentEntity)
    {
        this.memberEntity = memberEntity;
        this.commentEntity = commentEntity;
    }
}
