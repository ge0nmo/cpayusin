package com.cpayusin.comment.domain;

import com.cpayusin.comment.domain.type.CommentType;
import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Table(
        indexes = @Index(name = "idx_comment_created", columnList = "created_at")
)
@Entity
public class Comment extends BaseEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Boolean isRemoved = false;

    @Column(nullable = false, updatable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Version
    private Integer version;

    @Builder
    public Comment(String text)
    {
        this.text = text;
        this.type = CommentType.PARENT_COMMENT.getCode();
    }

    public void addPost(Post post)
    {
        this.post = post;
    }

    public void addParent(Comment parent)
    {
        if(this.parent != null)
            this.parent.getChildren().remove(this);

        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void addMember(Member member)
    {
        this.member = member;
    }

    public void updateText(String text)
    {
        this.text = text;
    }

    public void deleteComment()
    {
        this.isRemoved = true;
    }

}
