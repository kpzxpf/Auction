package com.volzhin.auction.service.cache;

import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.repository.cache.LotCacheRepository;
import com.volzhin.auction.service.image.ImageService;
import com.volzhin.auction.service.LotService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotCacheService {
    private final LotService lotService;
    private final LotCacheRepository lotCacheRepository;
    private final ImageService imageService;

    @Value("${spring.data.redis.cache-size}")
    private int cacheSize;
    @Value("${spring.data.redis.lot-cache-entry-threshold-minutes}")
    private int lotCacheEntryThresholdMinutes;

    @PostConstruct
    public void init() {
        fillCache();
    }

    @Scheduled(cron = "0 */6 * * * *")
    public void fillCache() {
        log.info("Fill cache");

        List<Lot> lots = lotService.findLotsEndingWithin(lotCacheEntryThresholdMinutes, cacheSize);
        lotCacheRepository.saveAll(convertLotsToLotCaches(lots));

        log.info("Finish fill cache");
    }

    public void deleteLot(long id) {
        lotCacheRepository.deleteById(id);
    }

    public List<LotCache> getCacheLotsByCategoryName(String categoryName) {
        return (List<LotCache>) lotCacheRepository.findByCategoryName(categoryName);
    }

    public List<LotCache> getCacheLots() {
        return (List<LotCache>) lotCacheRepository.findAll();
    }

    public void updateLot(LotCache lotCache) {
        if (lotCacheRepository.existsById(lotCache.getId())) {
            lotCacheRepository.save(lotCache);
        }
    }

    public boolean existsLotById(Long id) {
        return lotCacheRepository.existsById(id);
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
