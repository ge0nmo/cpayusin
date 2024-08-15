package com.cpayusin.comment.infrastructure;

import com.cpayusin.comment.domain.type.CommentType;
import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
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
@Table(name = "comment",
        indexes = @Index(name = "idx_comment_created", columnList = "created_at")
)
@Entity
public class CommentEntity extends BaseEntity
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
    private PostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommentEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentEntity> children = new ArrayList<>();

    @Version
    private Integer version;

    @Builder
    public CommentEntity(String text)
    {
        this.text = text;
        this.type = CommentType.PARENT_COMMENT.getCode();
        this.voteCount = 0;
    }

    public void addPost(PostEntity post)
    {
        this.postEntity = post;
    }

    public void addParent(CommentEntity parent)
    {
        if(this.parent != null)
            this.parent.getChildren().remove(this);

        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void addMember(MemberEntity memberEntity)
    {
        this.memberEntity = memberEntity;
    }

    public void updateText(String text)
    {
        this.text = text;
    }

    public void upVote()
    {
        this.voteCount++;
    }

    public void downVote()
    {
        this.voteCount--;
    }

    public void deleteComment()
    {
        this.isRemoved = true;
    }

}
