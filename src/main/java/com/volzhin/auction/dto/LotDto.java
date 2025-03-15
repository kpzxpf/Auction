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
    @NotNull(message = "Start time не должен быть null")
    @FutureOrPresent(message = "Start time должен быть в настоящем или будущем")
    private LocalDateTime startTime;
    @NotNull(message = "End time не должен быть null")
    @Future(message = "End time должен быть в будущем")
    private LocalDateTime endTime;
    private Lot.Status status;
    private long sellerId;
    @NotNull
    private int categoryId;
}
