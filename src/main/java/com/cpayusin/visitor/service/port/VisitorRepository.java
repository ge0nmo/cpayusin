package com.cpayusin.visitor.service.port;

import com.cpayusin.visitor.infrastructure.VisitorEntity;

import java.time.LocalDate;

public interface VisitorRepository
{
    boolean existsByIpAddressAndDate(String ipAddress, LocalDate date);

    long countByDate(LocalDate date);

    long count();

    VisitorEntity save(VisitorEntity visitor);
}
