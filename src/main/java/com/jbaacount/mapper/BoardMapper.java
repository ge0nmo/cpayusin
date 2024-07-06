package com.jbaacount.mapper;

import com.jbaacount.model.Board;
import com.jbaacount.payload.request.board.BoardCreateRequest;
import com.jbaacount.payload.request.board.BoardUpdateRequest;
import com.jbaacount.payload.request.board.CategoryUpdateRequest;
import com.jbaacount.payload.response.board.BoardChildrenResponse;
import com.jbaacount.payload.response.board.BoardCreateResponse;
import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.payload.response.board.BoardResponse;
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

    Board toBoardEntity(BoardCreateRequest request);

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


    List<BoardMenuResponse> toBoardMenuResponse(List<Board> boards);

    @Mapping(target = "category", ignore = true)
    BoardMenuResponse toMenuResponse(Board board);

    @Mapping(target = "parentId", source = "parent.id")
    BoardChildrenResponse toChildrenResponse(Board board);

    List<BoardChildrenResponse> toChildrenList(List<Board> boards);
}
