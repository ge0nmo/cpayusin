package com.cpayusin.file.infrastructure;

import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class FileEntity extends BaseEntity
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
    private PostEntity postEntity;

    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Builder
    public FileEntity(String uploadFileName, String storeFileName, String url, String contentType)
    {
        this.uploadFileName = uploadFileName;
        this.storedFileName = storeFileName;
        this.url = url;
        this.contentType = contentType;
    }

    public void addPost(PostEntity postEntity)
    {
        this.postEntity = postEntity;
    }

    public void addMember(MemberEntity memberEntity)
    {
        this.memberEntity = memberEntity;
    }
}
