package com.cpayusin.board.infrastructure;

import com.cpayusin.common.domain.BaseEntity;
import com.cpayusin.board.domain.type.BoardType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "board",
        indexes = @Index(name = "idx_board_order", columnList = "orderIndex")
)
@Setter
@Entity
public class BoardEntity extends BaseEntity
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
    private BoardEntity parent;

    @OneToMany(mappedBy = "parent")
    private List<BoardEntity> children = new ArrayList<>();

    @Builder
    public BoardEntity(String name, Boolean isAdminOnly, Integer orderIndex)
    {
        this.name = name;
        this.isAdminOnly = isAdminOnly;
        this.orderIndex = orderIndex;
    }

    public void addParent(BoardEntity board)
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
