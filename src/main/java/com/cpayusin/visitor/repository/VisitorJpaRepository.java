package com.cpayusin.visitor.repository;

import com.cpayusin.visitor.domain.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitorJpaRepository extends JpaRepository<Visitor, Long>
{
    boolean existsByIpAddressAndDate(String ipAddress, LocalDate date);

    long countByDate(LocalDate date);

}
