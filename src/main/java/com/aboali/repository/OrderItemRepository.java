// src/main/java/com/aboali/repository/OrderItemRepository.java
package com.aboali.repository;

import com.aboali.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // ── All items for a specific order ───────────────────────────
    List<OrderItem> findByOrderId(Long orderId);
    
    // ── All items for a specific product ────────────────────────
    List<OrderItem> findByProductId(Long productId);
    
    // ── Top N best-selling products by quantity ──────────────────
    @Query("""
        SELECT   oi.name,
                 oi.productId,
                 SUM(oi.quantity)  AS totalQty,
                 SUM(oi.subtotal)  AS totalRevenue
        FROM     OrderItem oi
        JOIN     oi.order o
        WHERE    o.createdAt BETWEEN :from AND :to
        GROUP BY oi.name, oi.productId
        ORDER BY totalQty DESC
        LIMIT    :limit
    """)
    List<Object[]> findTopProducts(
            @Param("from")  LocalDateTime from,
            @Param("to")    LocalDateTime to,
            @Param("limit") int limit
    );
    
    // ── Sales breakdown by category ──────────────────────────────
    @Query("""
        SELECT   p.category,
                 SUM(oi.quantity)  AS totalQty,
                 SUM(oi.subtotal)  AS totalRevenue
        FROM     OrderItem oi
        JOIN     oi.order o
        LEFT JOIN Product p ON p.id = oi.productId
        WHERE    o.createdAt BETWEEN :from AND :to
        GROUP BY p.category
        ORDER BY totalRevenue DESC
    """)
    List<Object[]> findSalesByCategory(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );
    
    // ── Total quantity sold for a product in a period ────────────
    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM   OrderItem oi
        JOIN   oi.order o
        WHERE  oi.productId = :productId
        AND    o.createdAt BETWEEN :from AND :to
    """)
    Long totalQtySoldByProduct(
            @Param("productId") Long productId,
            @Param("from")      LocalDateTime from,
            @Param("to")        LocalDateTime to
    );
    
    // ── Delete all items belonging to an order ───────────────────
    void deleteByOrderId(Long orderId);
}
