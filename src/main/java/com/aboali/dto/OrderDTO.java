// src/main/java/com/aboali/dto/OrderDTO.java
package com.aboali.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    
    // ── Read-only fields (populated by server) ──────────────────
    private Long          id;
    private String        orderNumber;
    private String        status;
    private LocalDateTime createdAt;
    
    // ── Required from client ─────────────────────────────────────
    @NotBlank(message = "طريقة الدفع مطلوبة")
    private String paymentMethod;   // CASH | CARD | TRANSFER
    
    @NotEmpty(message = "يجب أن يحتوي الطلب على عنصر واحد على الأقل")
    @Valid
    private List<OrderItemDTO> items;
    
    // ── Financials ───────────────────────────────────────────────
    @NotNull(message = "المجموع الفرعي مطلوب")
    @DecimalMin(value = "0.0", message = "المجموع الفرعي يجب أن يكون موجباً")
    private BigDecimal subtotal;
    
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal tax      = BigDecimal.ZERO;
    
    @NotNull(message = "الإجمالي مطلوب")
    @DecimalMin(value = "0.0", message = "الإجمالي يجب أن يكون موجباً")
    private BigDecimal total;
    
    private String notes;
}
