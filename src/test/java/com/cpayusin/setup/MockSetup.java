package com.cpayusin.setup;

import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.post.infrastructure.PostEntity;
import com.cpayusin.vote.infrastructure.VoteEntity;
import org.junit.jupiter.api.BeforeEach;

public class MockSetup extends DummyObject
{
    protected MemberEntity mockMemberEntity;
    protected MemberEntity mockMemberEntity2;
    protected BoardEntity mockBoard1;
    protected PostEntity mockPost;
    protected CommentEntity mockCommentEntity;

    protected VoteEntity postVote;

    protected VoteEntity commentVote;

    protected BoardEntity mockBoard2;
    protected BoardEntity mockBoard3;

    @BeforeEach
    void setUp()
    {
        mockMemberEntity = newMockMember(1L, "test@gmail.com", "test", "ADMIN");
        mockMemberEntity2 = newMockMember(2L, "test2@gmail.com", "test2", "ADMIN");

        mockBoard1 = newMockBoard(1L, "first boardEntity", 1);
        mockPost = newMockPost(1L, "first postEntity", "content", mockBoard1, mockMemberEntity);
        mockCommentEntity = newMockComment(1L, "text", mockPost, mockMemberEntity);

        postVote = newMockPostVote(1L, mockMemberEntity2, mockPost);
        commentVote = newMockCommentVote(1L, mockMemberEntity2, mockCommentEntity);

        mockBoard2 = newMockBoard(2L, "second boardEntity", 2);
        mockBoard3 = newMockBoard(3L, "third boardEntity", 3);
    }
}
