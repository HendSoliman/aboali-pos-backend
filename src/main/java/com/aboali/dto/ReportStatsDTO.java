// src/main/java/com/arabicpos/dto/ReportStatsDTO.java
package com.aboali.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportStatsDTO {
    private BigDecimal totalSales;
    private Long totalOrders;
    private BigDecimal avgOrderValue;
    private String topProduct;
    private List<DailySalesDTO> dailySales;
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DailySalesDTO {
        private String date;
        private BigDecimal total;
        private Long orderCount;
    }
}
