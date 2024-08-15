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

import static com.cpayusin.board.infrastructure.QBoardEntity.boardEntity;
import static com.cpayusin.member.infrastructure.QMemberEntity.memberEntity;
import static com.cpayusin.post.infrastructure.QPostEntity.postEntity;

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
                        postEntity.memberEntity.id.as("memberId"),
                        postEntity.memberEntity.nickname.as("memberName"),
                        postEntity.boardEntity.id.as("boardId"),
                        postEntity.boardEntity.name.as("boardName"),
                        postEntity.id.as("postId"),
                        postEntity.title.as("title"),
                        postEntity.voteCount.as("voteCount"),
                        postEntity.commentCount.as("commentCount"),
                        postEntity.createdAt.as("createdAt")
                        ))
                .from(postEntity)
                .join(postEntity.memberEntity, memberEntity)
                .join(postEntity.boardEntity, boardEntity)
                .where(ltPostId(lastPost))
                .where(postEntity.boardEntity.id.in(boardIds))
                .orderBy(postEntity.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return paginationUtils.toSlice(pageable, fetchedResult);
    }

    private BooleanExpression ltPostId(Long postId)
    {
        if(postId == null)
            return null;

        return postEntity.id.lt(postId);
    }

}
