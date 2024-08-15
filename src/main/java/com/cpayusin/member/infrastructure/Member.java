package com.cpayusin.member.infrastructure;

import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.domain.type.Platform;
import jakarta.persistence.*;
import lombok.*;

@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String url;

    private String role;

    private boolean isRemoved;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Platform platform = Platform.HOME;

    public static Member from(MemberDomain memberDomain)
    {
        Member member = new Member();
        member.id = memberDomain.getId();
        member.nickname = memberDomain.getNickname();
        member.email = memberDomain.getEmail();
        member.password = memberDomain.getPassword();
        member.url = memberDomain.getUrl();
        member.role = memberDomain.getRole();
        member.platform = memberDomain.getPlatform();
        return member;
    }


    public void updatePassword(String password)
    {
        this.password = password;
    }

    public void updateNickname(String nickname)
    {
        this.nickname = nickname;
    }


    public MemberDomain toModel()
    {
        return MemberDomain.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .password(password)
                .url(url)
                .role(role)
                .platform(platform)
                .isRemoved(isRemoved)
                .build();
    }
}
