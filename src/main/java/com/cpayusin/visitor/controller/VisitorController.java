package com.cpayusin.visitor.controller;

import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.visitor.controller.response.VisitorResponse;
import com.cpayusin.visitor.controller.port.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/visitor")
@RequiredArgsConstructor
public class VisitorController
{
    private final VisitorService visitorService;

    @GetMapping
    public ResponseEntity<GlobalResponse<VisitorResponse>> getVisitors()
    {
        VisitorResponse data = visitorService.getVisitorResponse();

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }
}
