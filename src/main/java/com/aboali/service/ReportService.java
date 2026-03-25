// src/main/java/com/aboali/service/ReportService.java
package com.aboali.service;

import com.aboali.dto.ReportStatsDTO;
import com.aboali.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final OrderRepository orderRepo;
    
    public ReportStatsDTO getStats(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.atTime(23, 59, 59);
        
        BigDecimal totalSales = orderRepo.sumTotalBetween(start, end);
        Long       totalOrders = orderRepo.countBetween(start, end);
        
        BigDecimal avg = totalOrders > 0
                ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Top product
        List<Object[]> topRows = orderRepo.findTopProduct(start, end);
        String topProduct = topRows.isEmpty() ? "لا يوجد" : (String) topRows.get(0)[0];
        
        // Daily chart data
        List<ReportStatsDTO.DailySalesDTO> daily = orderRepo
                .findDailySales(start, end).stream()
                .map(row -> ReportStatsDTO.DailySalesDTO.builder()
                                                        .date(row[0].toString())
                                                        .total((BigDecimal) row[1])
                                                        .orderCount(((Number) row[2]).longValue())
                                                        .build())
                .toList();
        
        return ReportStatsDTO.builder()
                             .totalSales(totalSales)
                             .totalOrders(totalOrders)
                             .avgOrderValue(avg)
                             .topProduct(topProduct)
                             .dailySales(daily)
                             .build();
    }
}
