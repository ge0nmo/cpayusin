package com.cpayusin.post.infrastructure;


import com.cpayusin.common.utils.PaginationUtils;
import com.cpayusin.post.controller.response.PostMultiResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cpayusin.board.infrastructure.QBoard.board;
import static com.cpayusin.member.infrastructure.QMember.member;
import static com.cpayusin.post.infrastructure.QPost.post;

@RequiredArgsConstructor
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository
{
    private final JPAQueryFactory query;
    private final PaginationUtils paginationUtils;

    @Override
    public Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost, Pageable pageable)
    {
        List<PostMultiResponse> fetchedResult = query
                .select(Projections.fields(PostMultiResponse.class,
                        post.member.id.as("memberId"),
                        post.member.nickname.as("memberName"),
                        post.board.id.as("boardId"),
                        post.board.name.as("boardName"),
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.voteCount.as("voteCount"),
                        post.commentCount.as("commentCount"),
                        post.createdAt.as("createdAt")
                        ))
                .from(post)
                .join(post.member, member)
                .join(post.board, board)
                .where(ltPostId(lastPost))
                .where(post.board.id.in(boardIds))
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return paginationUtils.toSlice(pageable, fetchedResult);
    }

    private BooleanExpression ltPostId(Long postId)
    {
        if(postId == null)
            return null;

        return post.id.lt(postId);
    }

}
