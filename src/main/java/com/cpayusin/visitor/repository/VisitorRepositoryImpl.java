package com.cpayusin.visitor.repository;

import com.cpayusin.visitor.domain.Visitor;
import com.cpayusin.visitor.service.port.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Repository
public class VisitorRepositoryImpl implements VisitorRepository
{
    private final VisitorJpaRepository visitorJpaRepository;

    @Override
    public boolean existsByIpAddressAndDate(String ipAddress, LocalDate date)
    {
        return visitorJpaRepository.existsByIpAddressAndDate(ipAddress, date);
    }

    @Override
    public long countByDate(LocalDate date)
    {
        return visitorJpaRepository.countByDate(date);
    }

    @Override
    public long count()
    {
        return visitorJpaRepository.count();
    }

    @Override
    public Visitor save(Visitor visitor)
    {
        return visitorJpaRepository.save(visitor);
    }
}
