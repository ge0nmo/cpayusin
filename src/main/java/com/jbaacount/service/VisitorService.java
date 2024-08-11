package com.jbaacount.service;

import com.jbaacount.model.Visitor;
import com.jbaacount.payload.response.VisitorResponse;

import java.time.LocalDate;

public interface VisitorService
{
    Visitor save(String ipAddress, LocalDate date);

    void updateVisitorData();

    VisitorResponse getVisitorResponse();

}
