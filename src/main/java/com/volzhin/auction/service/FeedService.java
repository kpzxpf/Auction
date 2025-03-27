package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.cache.LotCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final LotCacheService lotCacheService;
    private final LotService lotService;
    private final LotMapper lotMapper;

    public List<LotDto> getLotFeed(int page, int size, String categoryName) {
        List<LotCache> cachedLots = getCacheLots(categoryName);
        List<LotDto> lotsFromCache = lotMapper.toDtos(cachedLots);

        int totalRequired = page == 0 ? 1 : page * size;
        int cachedCount = lotsFromCache.size();

        if (cachedCount >= totalRequired) {
            return lotsFromCache.subList(0, totalRequired);
        } else {
            int remaining = totalRequired - cachedCount;
            return combineLots(lotsFromCache, page, remaining, categoryName);
        }
    }

    private List<LotCache> getCacheLots(String categoryName) {
        if (categoryName == null) {
            return lotCacheService.getCacheLots();
        } else {
            return lotCacheService.getCacheLotsByCategoryName(categoryName);
        }
    }

    private List<LotDto> combineLots(List<LotDto> lotsFromCache, int page, int remaining, String categoryName) {
        List<LotDto> lotsFromDb = lotMapper.toDto(lotService.getLots(page,
                remaining, categoryName));

        List<LotDto> combinedLots = new ArrayList<>(lotsFromCache);
        combinedLots.addAll(lotsFromDb);

        return combinedLots;
    }
}
