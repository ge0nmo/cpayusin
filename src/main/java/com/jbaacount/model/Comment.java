package com.jbaacount.model;

import com.jbaacount.global.audit.BaseEntity;
import com.jbaacount.model.type.CommentType;
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

    private String type;

    private Integer voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String text)
    {
        this.text = text;
        this.type = CommentType.PARENT_COMMENT.getCode();
        this.voteCount = 0;
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
