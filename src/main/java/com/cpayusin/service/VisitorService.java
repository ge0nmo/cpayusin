package com.cpayusin.service;

import com.cpayusin.model.Visitor;
import com.cpayusin.payload.response.VisitorResponse;

import java.time.LocalDate;

public interface VisitorService
{
    Visitor save(String ipAddress, LocalDate date);

    void updateVisitorData();

    VisitorResponse getVisitorResponse();

}
