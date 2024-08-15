package com.cpayusin.visitor.controller.port;

import com.cpayusin.visitor.infrastructure.VisitorEntity;
import com.cpayusin.visitor.controller.response.VisitorResponse;

import java.time.LocalDate;

public interface VisitorService
{
    VisitorEntity save(String ipAddress, LocalDate date);

    void updateVisitorData();

    VisitorResponse getVisitorResponse();

}
