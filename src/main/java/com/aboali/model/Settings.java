
package com.aboali.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Settings {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String key;
    
    private String value;
    private String category;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
