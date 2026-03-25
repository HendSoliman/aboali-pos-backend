// src/main/java/com/aboali/controller/ReportController.java
package com.aboali.controller;

import com.aboali.dto.*;
import com.aboali.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService service;
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ReportStatsDTO>> getStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        // Default: current month
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to   == null) to   = LocalDate.now();
        
        return ResponseEntity.ok(ApiResponse.ok(service.getStats(from, to)));
    }
}
