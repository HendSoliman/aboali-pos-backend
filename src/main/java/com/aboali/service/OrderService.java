// src/main/java/com/aboali/service/OrderService.java
package com.aboali.service;

import com.aboali.dto.*;
import com.aboali.exception.ResourceNotFoundException;
import com.aboali.model.*;
import com.aboali.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository  orderRepo;
    private final ProductRepository productRepo;
    
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        // Build order number: POS-20260324-001
        String orderNum = "POS-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        
        Order order = Order.builder()
                           .orderNumber(orderNum)
                           .status("COMPLETED")
                           .paymentMethod(dto.getPaymentMethod())
                           .subtotal(dto.getSubtotal())
                           .discount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO)
                           .tax(dto.getTax()      != null ? dto.getTax()      : BigDecimal.ZERO)
                           .total(dto.getTotal())
                           .notes(dto.getNotes())
                           .build();
        
        // Map items + deduct stock
        List<OrderItem> items = dto.getItems().stream().map(itemDTO -> {
            // Deduct stock
            if (itemDTO.getProductId() != null) {
                productRepo.findById(itemDTO.getProductId()).ifPresent(p -> {
                    p.setStock(Math.max(0, p.getStock() - itemDTO.getQuantity()));
                    productRepo.save(p);
                });
            }
            return OrderItem.builder()
                            .order(order)
                            .productId(itemDTO.getProductId())
                            .name(itemDTO.getName())
                            .price(itemDTO.getPrice())
                            .quantity(itemDTO.getQuantity())
                            .subtotal(itemDTO.getSubtotal())
                            .build();
        }).toList();
        
        order.setItems(items);
        return toDTO(orderRepo.save(order));
    }
    
    public Page<OrderDTO> getOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepo.findAllByOrderByCreatedAtDesc(pageable).map(this::toDTO);
    }
    
    public OrderDTO getById(Long id) {
        return toDTO(orderRepo.findById(id)
                              .orElseThrow(() -> new ResourceNotFoundException("الطلب غير موجود")));
    }
    
    // ── Mapper ───────────────────────────────────────────────────
    private OrderDTO toDTO(Order o) {
        return OrderDTO.builder()
                       .id(o.getId()).orderNumber(o.getOrderNumber())
                       .paymentMethod(o.getPaymentMethod())
                       .subtotal(o.getSubtotal()).discount(o.getDiscount())
                       .tax(o.getTax()).total(o.getTotal())
                       .notes(o.getNotes()).createdAt(o.getCreatedAt())
                       .items(o.getItems().stream().map(i -> OrderItemDTO.builder()
                                                                         .productId(i.getProductId())
                                                                         .name(i.getName()).price(i.getPrice())
                                                                         .quantity(i.getQuantity()).subtotal(i.getSubtotal())
                                                                         .build()).toList())
                       .build();
    }
}
