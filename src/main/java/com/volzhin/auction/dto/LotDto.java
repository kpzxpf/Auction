package com.volzhin.auction.dto;

import com.volzhin.auction.entity.Lot;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Size(max = 200)
    private String description;
    @Positive
    private BigDecimal startingPrice;
    @Positive
    private BigDecimal currentPrice;
    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;
    @NotNull
    @Future
    private LocalDateTime endTime;
    private Lot.Status status;
    private long sellerId;
    @NotNull
    private String categoryName;
}
