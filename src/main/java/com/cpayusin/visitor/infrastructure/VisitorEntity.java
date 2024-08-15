package com.cpayusin.visitor.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "visitor")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VisitorEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;

    private LocalDate date;

    @Builder
    public VisitorEntity(String ipAddress, LocalDate date)
    {
        this.ipAddress = ipAddress;
        this.date = date;
    }
}
