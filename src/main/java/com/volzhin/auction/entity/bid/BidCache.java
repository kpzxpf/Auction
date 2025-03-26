package com.volzhin.auction.entity.bid;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RedisHash("bid")
public class BidCache {
    private Long id;
    private long lotId;
    private long userId;
    private BigDecimal amount;
}
