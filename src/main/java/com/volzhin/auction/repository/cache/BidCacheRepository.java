package com.volzhin.auction.repository.cache;

import com.volzhin.auction.entity.bid.BidCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidCacheRepository extends CrudRepository<BidCache, Long> {
    BidCache findByLotId(Long lotId);
}
