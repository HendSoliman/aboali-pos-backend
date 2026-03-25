// src/main/java/com/arabicpos/repository/OrderRepository.java
package com.aboali.repository;

import com.aboali.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to
    );
    
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    BigDecimal sumTotalBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    Long countBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    @Query("""
        SELECT oi.name, SUM(oi.quantity) as qty
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.createdAt BETWEEN :from AND :to
        GROUP BY oi.name
        ORDER BY qty DESC
        LIMIT 1
    """)
    List<Object[]> findTopProduct(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    @Query("""
        SELECT CAST(o.createdAt AS date) as day,
               SUM(o.total) as total,
               COUNT(o.id)  as cnt
        FROM Order o
        WHERE o.createdAt BETWEEN :from AND :to
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY day ASC
    """)
    List<Object[]> findDailySales(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
