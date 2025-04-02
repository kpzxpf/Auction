package com.volzhin.auction.service.cache;

import com.volzhin.auction.entity.bid.BidCache;
import com.volzhin.auction.repository.cache.BidCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidCacheService {
    private final BidCacheRepository bidCacheRepository;

    public void saveBid(BidCache newBid) {
        BidCache existingBid = bidCacheRepository.findByLotId(newBid.getLotId());

        if (existingBid != null) {
            if (newBid.getAmount().compareTo(existingBid.getAmount()) > 0) {
                existingBid.setId(newBid.getId());
                existingBid.setAmount(newBid.getAmount());
                existingBid.setUserId(newBid.getUserId());

                bidCacheRepository.save(existingBid);
            }
        } else {
            bidCacheRepository.save(newBid);
        }
    }
}
