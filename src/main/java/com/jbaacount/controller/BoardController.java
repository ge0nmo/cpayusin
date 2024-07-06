package com.jbaacount.controller;

import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.payload.response.board.BoardResponse;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@RestController
public class BoardController
{
    private final BoardService boardService;

    @GetMapping("/menu")
    public ResponseEntity<GlobalResponse<List<BoardMenuResponse>>> getMenu()
    {
        var data = boardService.getMenuList();

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/single-info/{board-id}")
    public ResponseEntity<GlobalResponse<BoardResponse>> getBoardById(@PathVariable("board-id") Long boardId)
    {
        var data = boardService.findBoardById(boardId);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/category/{board-id}")
    public ResponseEntity<GlobalResponse<List<BoardResponse>>> getCategoryList(@PathVariable("board-id") Long boardId)
    {
        var data = boardService.findCategoryByBoardId(boardId);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

}
