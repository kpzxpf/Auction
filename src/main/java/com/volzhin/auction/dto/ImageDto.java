package com.volzhin.auction.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImageDto {
    private Long id;
    private String key;
    private long size;
    private String name;
}