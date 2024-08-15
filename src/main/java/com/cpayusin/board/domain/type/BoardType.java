package com.cpayusin.board.domain.type;

import lombok.Getter;

@Getter
public enum BoardType
{
    BOARD("Board"),
    CATEGORY("Category");

    private String code;

    BoardType(String code)
    {
        this.code = code;
    }
}
