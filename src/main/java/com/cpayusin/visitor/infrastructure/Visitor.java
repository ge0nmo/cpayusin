package com.cpayusin.visitor.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Visitor
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;

    private LocalDate date;

    @Builder
    public Visitor(String ipAddress, LocalDate date)
    {
        this.ipAddress = ipAddress;
        this.date = date;
    }
}
