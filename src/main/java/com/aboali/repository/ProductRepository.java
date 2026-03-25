// src/main/java/com/arabicpos/repository/ProductRepository.java
package com.aboali.repository;

import com.aboali.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByActiveTrueOrderByNameAsc();
    
    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true AND (
            LOWER(p.name)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.nameAr) LIKE LOWER(CONCAT('%', :q, '%')) OR
            p.barcode = :q
        )
    """)
    List<Product> searchProducts(@Param("q") String query);
    
    Optional<Product> findByBarcode(String barcode);
    
    List<Product> findByCategoryAndActiveTrue(String category);
}
