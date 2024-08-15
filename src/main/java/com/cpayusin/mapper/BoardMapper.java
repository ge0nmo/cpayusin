package com.cpayusin.mapper;

import com.cpayusin.board.infrastructure.BoardEntity;
import com.cpayusin.board.controller.request.BoardCreateRequest;
import com.cpayusin.board.controller.request.BoardUpdateRequest;
import com.cpayusin.board.controller.request.CategoryUpdateRequest;
import com.cpayusin.board.controller.response.BoardChildrenResponse;
import com.cpayusin.board.controller.response.BoardCreateResponse;
import com.cpayusin.board.controller.response.BoardMenuResponse;
import com.cpayusin.board.controller.response.BoardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BoardMapper
{
    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

    @Mapping(target = "orderIndex", ignore = true)
    BoardEntity toBoardEntity(BoardCreateRequest request);

    @Mapping(target = "parentId", ignore = true)
    BoardCreateResponse toBoardCreateResponse(BoardEntity board);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateBoard(BoardUpdateRequest request, @MappingTarget BoardEntity board);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateBoard(CategoryUpdateRequest request, @MappingTarget BoardEntity board);


    @Mapping(target = "parentId", source = "parent.id")
    BoardResponse boardToResponse(BoardEntity entity);

    List<BoardResponse> toBoardResponseList(List<BoardEntity> boards);


    List<BoardMenuResponse> toBoardMenuResponse(List<BoardEntity> boards);

    @Mapping(target = "category", ignore = true)
    BoardMenuResponse toMenuResponse(BoardEntity board);

    @Mapping(target = "parentId", source = "parent.id")
    BoardChildrenResponse toChildrenResponse(BoardEntity board);

    List<BoardChildrenResponse> toChildrenList(List<BoardEntity> boards);
}
