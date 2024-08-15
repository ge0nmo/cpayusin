package com.cpayusin.visitor.service.port;

import com.cpayusin.visitor.infrastructure.Visitor;

import java.time.LocalDate;

public interface VisitorRepository
{
    boolean existsByIpAddressAndDate(String ipAddress, LocalDate date);

    long countByDate(LocalDate date);

    long count();

    Visitor save(Visitor visitor);
}
