package com.cpayusin.file.domain;

import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class File extends BaseEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uploadFileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public File(String uploadFileName, String storeFileName, String url, String contentType)
    {
        this.uploadFileName = uploadFileName;
        this.storedFileName = storeFileName;
        this.url = url;
        this.contentType = contentType;
    }

    public void addPost(Post post)
    {
        this.post = post;
    }

    public void addMember(Member member)
    {
        this.member = member;
    }
}