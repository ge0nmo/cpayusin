package com.cpayusin.mapper;

import com.cpayusin.comment.controller.response.*;
import com.cpayusin.comment.infrastructure.CommentEntity;
import com.cpayusin.comment.controller.request.CommentCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper
{
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
    CommentEntity toCommentEntity(CommentCreateRequest request);

    CommentCreatedResponse toCommentCreatedResponse(CommentEntity commentEntity);

    CommentUpdateResponse toCommentUpdateResponse(CommentEntity commentEntity);

    default CommentSingleResponse toCommentSingleResponse(CommentEntity entity, boolean voteStatus)
    {
        Long parentId = null;
        if(entity.getParent() != null)
            parentId = entity.getParent().getId();

        return CommentSingleResponse.builder()
                .boardId(entity.getPostEntity().getBoardEntity().getId())
                .boardName(entity.getPostEntity().getBoardEntity().getName())
                .postId(entity.getPostEntity().getId())
                .postTitle(entity.getPostEntity().getTitle())
                .memberId(entity.getMemberEntity().getId())
                .nickname(entity.getMemberEntity().getNickname())
                .memberProfile(entity.getMemberEntity().getUrl())
                .commentId(entity.getId())
                .parentId(parentId)
                .text(entity.getText())
                .voteCount(entity.getVoteCount())
                .voteStatus(voteStatus)
                .isRemoved(entity.getIsRemoved())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Mapping(target = "memberId", source = "memberEntity.id")
    @Mapping(target = "memberName", source = "memberEntity.nickname")
    @Mapping(target = "memberProfile", source = "memberEntity.url")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "voteStatus", ignore = true)
    CommentChildrenResponse toCommentChildrenResponse(CommentEntity commentEntity);

    List<CommentChildrenResponse> toCommentChildrenResponseList(List<CommentEntity> commentEntities);

    @Mapping(target = "memberId", source = "memberEntity.id")
    @Mapping(target = "memberName", source = "memberEntity.nickname")
    @Mapping(target = "memberProfile", source = "memberEntity.url")
    @Mapping(target = "voteStatus", ignore = true)
    CommentResponse toCommentParentResponse(CommentEntity commentEntity);

    List<CommentResponse> toCommentParentResponseList(List<CommentEntity> commentEntities);
}
