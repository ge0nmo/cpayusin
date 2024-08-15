package com.cpayusin.file.infrastructure;

import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.file.domain.FileDomain;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.post.infrastructure.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

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

    public static File from(FileDomain fileDomain)
    {
        File file = new File();
        file.uploadFileName = fileDomain.getUploadFileName();
        file.storedFileName = fileDomain.getStoredFileName();
        file.url = fileDomain.getUrl();
        file.contentType = fileDomain.getContentType();

        file.member = Member.from(fileDomain.getMemberDomain());
        // TODO post
        return file;
    }

    public FileDomain toModel()
    {
        return FileDomain.builder()
                .id(id)
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .url(url)
                .contentType(contentType)
                .memberDomain(member.toModel())
                .postDomain(Optional.ofNullable(post)
                        .map(Post::toModel)
                        .orElse(null))
                .build();
    }
}
