// src/main/java/com/aboali/service/ProductService.java
package com.aboali.service;

import com.aboali.dto.ProductDTO;
import com.aboali.exception.ResourceNotFoundException;
import com.aboali.model.Product;
import com.aboali.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository repo;
    
    public List<ProductDTO> getAll() {
        return repo.findByActiveTrueOrderByNameAsc()
                   .stream().map(this::toDTO).toList();
    }
    
    public List<ProductDTO> search(String query) {
        return repo.searchProducts(query)
                   .stream().map(this::toDTO).toList();
    }
    
    public ProductDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }
    
    public ProductDTO getByBarcode(String barcode) {
        return repo.findByBarcode(barcode)
                   .map(this::toDTO)
                   .orElseThrow(() -> new ResourceNotFoundException("المنتج غير موجود"));
    }
    
    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product p = toEntity(dto);
        return toDTO(repo.save(p));
    }
    
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product p = findOrThrow(id);
        p.setName(dto.getName());
        p.setNameAr(dto.getNameAr());
        p.setBarcode(dto.getBarcode());
        p.setCategory(dto.getCategory());
        p.setPrice(dto.getPrice());
        p.setCost(dto.getCost());
        p.setStock(dto.getStock());
        p.setEmoji(dto.getEmoji());
        return toDTO(repo.save(p));
    }
    
    @Transactional
    public void delete(Long id) {
        Product p = findOrThrow(id);
        p.setActive(false);   // soft delete
        repo.save(p);
    }
    
    // ── Helpers ──────────────────────────────────────────────────
    private Product findOrThrow(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new ResourceNotFoundException("المنتج غير موجود: " + id));
    }

    private ProductDTO toDTO(Product p) {
        return ProductDTO.builder()
                         .id(p.getId())
                         .name(p.getName()).nameAr(p.getNameAr())
                         .barcode(p.getBarcode()).category(p.getCategory())
                         .price(p.getPrice()).cost(p.getCost())
                         .unit(p.getUnit())
                         .stock(p.getStock()).emoji(p.getEmoji()).active(p.getActive())
                         .isLoose(p.getIsLoose())
                         .build();
    }
    
    private Product toEntity(ProductDTO dto) {
        return Product.builder()
                      .name(dto.getName()).nameAr(dto.getNameAr())
                      .barcode(dto.getBarcode()).category(dto.getCategory())
                      .unit(dto.getUnit())
                      .isLoose(dto.getIsLoose())
                      .price(dto.getPrice()).cost(dto.getCost() != null ? dto.getCost() : java.math.BigDecimal.ZERO)
                      .stock(dto.getStock()).emoji(dto.getEmoji()).active(true)
                      .build();
    }
}
