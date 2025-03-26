package com.volzhin.auction.controller;

import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.service.cache.LotCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final LotCacheService lotCacheService;

    @GetMapping
    public List<LotCache> getLotCaches() {
        return lotCacheService.getCacheLots();
    }
}
