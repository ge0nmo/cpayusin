package com.cpayusin.board.domain.type;

import lombok.Getter;

@Getter
public enum BoardType
{
    BOARD("BoardEntity"),
    CATEGORY("Category");

    private String code;

    BoardType(String code)
    {
        this.code = code;
    }
}
