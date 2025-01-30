package com.volzhin.auction.dto;

import com.volzhin.auction.entity.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LotDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 200)
    private String title;
    @Max(value = 200)
    private String description;
    @Positive
    private BigDecimal startingPrice;
    @Positive
    private BigDecimal currentPrice;
    private long seller_id;
    private Category category;
}
