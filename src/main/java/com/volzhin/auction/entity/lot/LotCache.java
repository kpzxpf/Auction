package com.volzhin.auction.entity.lot;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RedisHash("lot")
public class LotCache implements Serializable {
    private long id;
    private String title;
    private String description;
    private List<String> urlImages;
    private LocalDateTime endTime;
}
