package com.cpayusin.board.infrastructure;

import com.cpayusin.board.domain.BoardDomain;
import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.board.domain.type.BoardType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        indexes = @Index(name = "idx_board_order", columnList = "orderIndex")
)
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
    @JoinColumn(name = "parent_id")
    private Board parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> children = new ArrayList<>();

    @Builder
    public Board(String name, Boolean isAdminOnly, Integer orderIndex)
    {
        this.name = name;
        this.isAdminOnly = isAdminOnly;
        this.orderIndex = orderIndex;
    }

    public void addParent(Board board)
    {
        if(this.getParent() != null)
            board.getChildren().remove(this);

        this.parent = board;
        board.getChildren().add(this);

        this.setType(BoardType.CATEGORY.getCode());
    }

    public static Board from(BoardDomain boardDomain)
    {
        Board board = new Board();

        board.name = boardDomain.getName();
        board.isAdminOnly = boardDomain.getIsAdminOnly();
        board.orderIndex = boardDomain.getOrderIndex();
        board.type = boardDomain.getType();

        if(boardDomain.getParent() != null)
            board.parent = Board.from(boardDomain.getParent());

        return board;
    }

    public BoardDomain toModel()
    {
        return BoardDomain.builder()
                .id(id)
                .name(name)
                .isAdminOnly(isAdminOnly)
                .orderIndex(orderIndex)
                .type(type)
                .build();
    }
}
