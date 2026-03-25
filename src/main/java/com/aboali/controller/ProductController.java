// src/main/java/com/aboali/controller/ProductController.java
package com.aboali.controller;

import com.aboali.dto.*;
import com.aboali.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAll(
            @RequestParam(required = false) String search
    ) {
        var data = (search != null && !search.isBlank())
                ? service.search(search)
                : service.getAll();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductDTO>> getByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByBarcode(barcode)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> create(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.ok("تم إضافة المنتج", service.create(dto)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> update(
            @PathVariable Long id, @Valid @RequestBody ProductDTO dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok("تم تحديث المنتج", service.update(id, dto)));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("تم حذف المنتج", null));
    }
}
