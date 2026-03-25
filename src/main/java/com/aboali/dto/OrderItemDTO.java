// src/main/java/com/arabicpos/dto/OrderItemDTO.java
package com.aboali.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemDTO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
