// src/main/java/com/aboali/controller/OrderController.java
package com.aboali.controller;

import com.aboali.dto.*;
import com.aboali.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.getOrders(page, size)));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.ok("تم إنشاء الطلب بنجاح", service.createOrder(dto)));
    }
}
