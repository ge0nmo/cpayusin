package com.cpayusin.board.mapper;

import com.cpayusin.board.domain.Board;
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

    @Mapping(target = "type", source = "type")
    @Mapping(target = "orderIndex", source = "orderIndex")
    Board toBoardEntity(BoardCreateRequest request, int orderIndex, String type);


    @Mapping(target = "parentId", ignore = true)
    BoardCreateResponse toBoardCreateResponse(Board board);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateBoard(BoardUpdateRequest request, @MappingTarget Board board);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateBoard(CategoryUpdateRequest request, @MappingTarget Board board);


    @Mapping(target = "parentId", source = "parent.id")
    BoardResponse boardToResponse(Board entity);

    List<BoardResponse> toBoardResponseList(List<Board> boards);



    @Mapping(target = "category", ignore = true)
    BoardMenuResponse toMenuResponse(Board board);

    @Mapping(target = "parentId", source = "parent.id")
    BoardChildrenResponse toChildrenResponse(Board board);

    List<BoardChildrenResponse> toChildrenList(List<Board> boards);
}
