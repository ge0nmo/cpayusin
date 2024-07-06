package com.jbaacount.setup;

import com.jbaacount.dummy.DummyObject;
import com.jbaacount.model.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class MockSetup extends DummyObject
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

        mockBoard1 = newMockBoard(1L, "first board", 1);
        mockPost = newMockPost(1L, "first post", "content", mockBoard1, mockMember);
        mockComment = newMockComment(1L, "text", mockPost, mockMember);

        postVote = newMockPostVote(1L, mockMember2, mockPost);
        commentVote = newMockCommentVote(1L, mockMember2, mockComment);

        mockBoard2 = newMockBoard(2L, "second board", 2);
        mockBoard3 = newMockBoard(3L, "third board", 3);

    }
}
