package com.volzhin.auction.service;

import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.repository.LotCacheRepository;
import com.volzhin.auction.service.image.ImageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final LotService lotService;
    private final LotCacheRepository lotCacheRepository;
    private final ImageService imageService;

    @Value("${spring.data.redis.cache-size}")
    private int cacheSize;

    @PostConstruct
    public void init() {
        log.info("Cache initialize");
        List<Lot> lots = lotService.findActiveLotsSortedByEndTime(cacheSize);

        lotCacheRepository.saveAll(convertLotsToLotCaches(lots));
        log.info("Cache initialized");
    }

    public List<LotCache> getCacheLots() {
        return (List<LotCache>) lotCacheRepository.findAll();
    }

    private List<LotCache> convertLotsToLotCaches(List<Lot> lots) {
        return lots.stream().map(lot -> LotCache.builder()
                        .id(lot.getId())
                        .title(lot.getTitle())
                        .description(lot.getDescription())
                        .urlImages(imageService.getImageUrlsByLotId(lot.getId()))
                        .build())
                .toList();
    }
}
