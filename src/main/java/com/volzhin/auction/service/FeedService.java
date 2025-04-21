package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.cache.LotCacheService;
import com.volzhin.auction.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final LotCacheService lotCacheService;
    private final LotService lotService;
    private final LotMapper lotMapper;
    private final ImageService imageService;

    public List<LotDto> getLotFeed(int page, int size, String categoryName) {
        int startIndex = page * size;
        int endIndex = startIndex + size;

        List<LotCache> cachedLots = retrieveCachedLots(categoryName);
        List<LotDto> lotsFromCache = lotMapper.toDtos(cachedLots);
        int cachedLotCount = lotsFromCache.size();

        List<LotDto> lotsToReturn = getLotsToReturn(startIndex, endIndex, cachedLotCount,
                lotsFromCache, page, size, categoryName);

        enrichWithImageUrls(lotsToReturn);

        return lotsToReturn.stream()
                .sorted(Comparator.comparing(LotDto::getEndTime))
                .filter(lot -> lot.getStatus() == Lot.Status.active)
                .toList();
    }

    private List<LotCache> retrieveCachedLots(String categoryName) {
        return categoryName == null ? lotCacheService.getCacheLots() :
                lotCacheService.getCacheLotsByCategoryName(categoryName);
    }

    private List<LotDto> getLotsToReturn(int startIndex, int endIndex, int cachedLotCount,
                                         List<LotDto> lotsFromCache, int page, int size, String categoryName) {
        if (cachedLotCount >= endIndex) {
            return lotsFromCache.subList(startIndex, endIndex);
        } else {
            List<Lot> lotsFromDatabase = lotService.getLots(page, size, categoryName);
            return lotMapper.toDto(lotsFromDatabase);
        }
    }

    private void enrichWithImageUrls(List<LotDto> lots) {
        for (LotDto lot : lots) {
            List<String> imageUrls = imageService.getImageUrlsByLotId(lot.getId());
            lot.setImageUrls(imageUrls);
        }
    }
}