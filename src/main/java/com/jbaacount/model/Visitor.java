package com.jbaacount.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Visitor
{
    @Id @GeneratedValue
    private Long id;

    private String ipAddress;

    private String userAgent;

    private LocalDate date;

    @Builder
    public Visitor(String ipAddress, String userAgent, LocalDate date)
    {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.date = date;
    }
}
