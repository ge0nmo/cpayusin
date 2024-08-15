package com.cpayusin.dummy;

import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.member.domain.type.Platform;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.vote.infrastructure.VoteEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyObject
{
    protected MemberEntity newMember(String email, String nickname)
    {
        return MemberEntity.builder()
                .email(email)
                .nickname(nickname)
                .platform(Platform.HOME)
                .password(getEncodedPassword())
                .build();
    }

    protected MemberEntity newMockMember(Long id, String email, String nickname, String role)
    {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(id)
                .email(email)
                .platform(Platform.HOME)
                .nickname(nickname)
                .password(getEncodedPassword())
                .role(role)
                .build();

        memberEntity.setCreatedAt(LocalDateTime.now());
        memberEntity.setModifiedAt(LocalDateTime.now());

        return memberEntity;
    }

    protected BoardEntity newBoard(String name, int orderIndex)
    {
        BoardEntity board = BoardEntity.builder()
                .isAdminOnly(false)
                .name(name)
                .build();

        board.setOrderIndex(orderIndex);
        board.setCreatedAt(LocalDateTime.now());
        board.setModifiedAt(LocalDateTime.now());

        return board;
    }

    protected BoardEntity newMockBoard(Long id, String name, int orderIndex)
    {
        BoardEntity board = BoardEntity.builder()
                .isAdminOnly(false)
                .name(name)
                .build();

        board.setId(id);
        board.setOrderIndex(orderIndex);
        board.setCreatedAt(LocalDateTime.now());
        board.setModifiedAt(LocalDateTime.now());

        return board;
    }

    protected PostEntity newPost(String title, String content, BoardEntity board, MemberEntity memberEntity)
    {
        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .build();

        post.addBoard(board);
        post.addMember(memberEntity);

        return post;
    }

    protected PostEntity newMockPost(Long id, String title, String content, BoardEntity board, MemberEntity memberEntity)
    {
        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .build();
        post.setId(id);
        post.addBoard(board);
        post.addMember(memberEntity);

        return post;
    }

    protected CommentEntity newMockComment(Long id, String text, PostEntity post, MemberEntity memberEntity)
    {
        CommentEntity commentEntity = CommentEntity.builder()
                .text(text)
                .build();

        commentEntity.setId(id);
        commentEntity.addPost(post);
        commentEntity.addMember(memberEntity);

        return commentEntity;
    }

    protected VoteEntity newMockPostVote(Long id, MemberEntity memberEntity, PostEntity post)
    {
        return new VoteEntity(memberEntity, post);
    }

    protected VoteEntity newMockCommentVote(Long id, MemberEntity memberEntity, CommentEntity commentEntity)
    {
        return new VoteEntity(memberEntity, commentEntity);
    }

    private String getEncodedPassword()
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode("123456789");
    }
}
