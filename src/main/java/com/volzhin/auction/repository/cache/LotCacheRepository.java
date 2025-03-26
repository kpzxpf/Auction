package com.volzhin.auction.repository.cache;

import com.volzhin.auction.entity.lot.LotCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotCacheRepository extends CrudRepository<LotCache, Long> {
}
