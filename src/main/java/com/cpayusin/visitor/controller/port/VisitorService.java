package com.cpayusin.visitor.controller.port;

import com.cpayusin.visitor.domain.Visitor;
import com.cpayusin.visitor.controller.response.VisitorResponse;

import java.time.LocalDate;

public interface VisitorService
{
    Visitor save(String ipAddress, LocalDate date);

    void updateVisitorData();

    VisitorResponse getVisitorResponse();

}
