package com.cpayusin.mapper;

import com.cpayusin.post.controller.response.*;
import com.cpayusin.post.infrastructure.Post;
import com.cpayusin.post.controller.request.PostCreateRequest;
import com.cpayusin.post.controller.request.PostUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper
{
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    Post toPostEntity(PostCreateRequest request);

    default void updatePostFromUpdateRequest(PostUpdateRequest postUpdateRequest, Post post)
    {
        if ( postUpdateRequest == null ) {
            return;
        }

        if ( postUpdateRequest.getTitle() != null ) {
            post.updateTitle( postUpdateRequest.getTitle() );
        }
        if ( postUpdateRequest.getContent() != null ) {
            post.updateContent( postUpdateRequest.getContent() );
        }
    }

    default PostSingleResponse toPostSingleResponse(Post entity, boolean voteStatus)
    {
        if( entity == null) {
            return null;
        }

        return PostSingleResponse.builder()
                .memberId(entity.getMember().getId())
                .boardId(entity.getBoard().getId())
                .boardName(entity.getBoard().getName())
                .nickname(entity.getMember().getNickname())
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .voteCount(entity.getVoteCount())
                .createdAt(entity.getCreatedAt())
                .voteStatus(voteStatus)
                .build();
    }


    @Mapping(target = "updatedAt", ignore = true)
    PostUpdateResponse toPostUpdateResponse(Post post);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "files", source = "files")
    PostCreateResponse toPostCreateResponse(Post post, List<String> files);

    default PostMultiResponse toPostMultiResponse(PostResponseProjection projection)
    {
        return PostMultiResponse.builder()
                .memberId(projection.getMemberId())
                .memberName(projection.getMemberName())
                .boardId(projection.getBoardId())
                .boardName(projection.getBoardName())
                .postId(projection.getPostId())
                .title(projection.getTitle())
                .voteCount(projection.getVoteCount())
                .commentsCount(projection.getCommentCount())
                .createdAt(projection.getCreatedAt())
                .build();
    }

    default List<PostMultiResponse> toPostMultiResponses(List<PostResponseProjection> projections)
    {
        if(projections == null || projections.isEmpty())
            return Collections.emptyList();

        return projections.stream()
                .map(this::toPostMultiResponse)
                .toList();
    }
}
