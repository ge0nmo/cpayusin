package com.cpayusin.visitor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitorJpaRepository extends JpaRepository<VisitorEntity, Long>
{
    boolean existsByIpAddressAndDate(String ipAddress, LocalDate date);

    long countByDate(LocalDate date);

}
