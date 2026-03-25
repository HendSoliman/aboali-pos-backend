
package com.aboali.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDTO {
    private Long id;
    
    @NotBlank(message = "اسم المنتج مطلوب")
    private String name;
    
    private String nameAr;
    private String barcode;
    private String category;
    
    @NotNull(message = "السعر مطلوب")
    @DecimalMin(value = "0.0", message = "السعر يجب أن يكون موجباً")
    private BigDecimal price;
    
    private BigDecimal cost;
    
    @NotNull @Min(0)
    private Integer stock;
    
    private String emoji;
    private Boolean active;

    private String  unit;
    
    @JsonProperty("isLoose")
    private Boolean isLoose;
    
}
