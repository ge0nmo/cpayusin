package com.cpayusin.setup;

import com.cpayusin.board.domain.Board;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.comment.domain.Comment;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import com.cpayusin.vote.domain.Vote;
import org.junit.jupiter.api.BeforeEach;

public class MockSetup extends DummyObject
{
    protected Member mockMember;
    protected Member mockMember2;
    protected Board mockBoard1;
    protected Post mockPost;
    protected Comment mockComment;

    protected Vote postVote;

    protected Vote commentVote;

    protected Board mockBoard2;
    protected Board mockBoard3;

    @BeforeEach
    void setUp()
    {
        mockMember = newMockMember(1L, "test@gmail.com", "test", "ADMIN");
        mockMember2 = newMockMember(2L, "test2@gmail.com", "test2", "ADMIN");

        mockBoard1 = newMockBoard(1L, "first boardEntity", 1);
        mockPost = newMockPost(1L, "first postEntity", "content", mockBoard1, mockMember);
        mockComment = newMockComment(1L, "text", mockPost, mockMember);

        postVote = newMockPostVote(1L, mockMember2, mockPost);
        commentVote = newMockCommentVote(1L, mockMember2, mockComment);

        mockBoard2 = newMockBoard(2L, "second boardEntity", 2);
        mockBoard3 = newMockBoard(3L, "third boardEntity", 3);
    }
}
