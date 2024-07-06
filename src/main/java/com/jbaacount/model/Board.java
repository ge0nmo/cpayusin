package com.jbaacount.model;

import com.jbaacount.global.audit.BaseEntity;
import com.jbaacount.model.type.BoardType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Board extends BaseEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isAdminOnly;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private String type = BoardType.BOARD.getCode();

    @ManyToOne(fetch = FetchType.LAZY)
    private Board parent;

    @OneToMany(mappedBy = "parent")
    private List<Board> children = new ArrayList<>();

    @Builder
    public Board(String name, Boolean isAdminOnly)
    {
        this.name = name;
        this.isAdminOnly = isAdminOnly;
    }

    public void addParent(Board board)
    {
        if(this.getParent() != null)
            board.getChildren().remove(this);

        this.parent = board;
        board.getChildren().add(this);

        this.setType(BoardType.CATEGORY.getCode());
    }

    public void updateBoardType(String boardType)
    {
        this.type = boardType;
    }

    public void updateOrderIndex(Integer orderIndex)
    {
        this.orderIndex = orderIndex;
    }
}
