package com.jbaacount.model;

import com.jbaacount.global.audit.BaseEntity;
import com.jbaacount.model.type.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, name = "score")
    private int score;

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

    public void getScoreByVote(int num)
    {
        this.score += num;
    }

    public void getScoreByPost()
    {
        this.score += 3;
    }

    public void getScoreByComment()
    {
        this.score += 1;
    }

    public void initializeScore()
    {
        this.score = 0;
    }
}
