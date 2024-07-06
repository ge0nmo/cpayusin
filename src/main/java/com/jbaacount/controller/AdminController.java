package com.jbaacount.controller;

import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.payload.request.board.BoardCreateRequest;
import com.jbaacount.payload.request.board.BoardUpdateRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.board.BoardCreateResponse;
import com.jbaacount.payload.response.board.BoardMenuResponse;
import com.jbaacount.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Component("adminController")
@RequestMapping("/api/v1/admin/manage")
public class AdminController
{
    private final BoardService boardService;

    @PostMapping("/board/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse<BoardCreateResponse>> saveBoard(@Valid @RequestBody BoardCreateRequest request,
                                                                         @AuthenticationPrincipal MemberDetails currentMember)
    {
        var data = boardService.createBoard(request, currentMember.getMember());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse<List<BoardMenuResponse>>> updateBoard(@Valid @RequestBody List<BoardUpdateRequest> request,
                                                                               @AuthenticationPrincipal MemberDetails currentMember)
    {
        var data = boardService.bulkUpdateBoards(request, currentMember.getMember());
        //var data = boardService.getMenuList();

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

}
