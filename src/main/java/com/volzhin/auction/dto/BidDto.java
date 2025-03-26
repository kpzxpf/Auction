package com.volzhin.auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BidDto {
    private long id;
    private long lotId;
    private long userId;
    @NotNull
    @Positive
    private BigDecimal amount;
}
