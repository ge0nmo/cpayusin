package com.cpayusin.member.infrastructure;

import com.cpayusin.common.domain.BaseEntity;
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
public class MemberEntity extends BaseEntity
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


    public void updatePassword(String password)
    {
        this.password = password;
    }

    public void updateNickname(String nickname)
    {
        this.nickname = nickname;
    }


}
