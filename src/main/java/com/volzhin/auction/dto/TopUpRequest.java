package com.volzhin.auction.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopUpRequest {
    private BigDecimal amount;
}
