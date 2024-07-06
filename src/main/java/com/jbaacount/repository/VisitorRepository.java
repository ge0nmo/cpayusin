package com.jbaacount.repository;

import com.jbaacount.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitorRepository extends JpaRepository<Visitor, Long>
{
    boolean existsByIpAddressAndDate(String ipAddress, LocalDate date);

    Long countByDate(LocalDate date);

}
