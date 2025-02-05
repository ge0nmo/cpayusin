package com.cpayusin.visitor.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@Table(
        name = "visitor",
        uniqueConstraints = {
                @UniqueConstraint(
                        name= "unique_ip_address_date",
                        columnNames = {"ip_address", "date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Visitor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    private LocalDate date;

    @Builder
    public Visitor(String ipAddress, LocalDate date)
    {
        this.ipAddress = ipAddress;
        this.date = date;
    }
}
