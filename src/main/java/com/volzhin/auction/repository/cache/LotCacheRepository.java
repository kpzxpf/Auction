package com.volzhin.auction.repository.cache;

import com.volzhin.auction.entity.lot.LotCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotCacheRepository extends CrudRepository<LotCache, Long> {

    List<LotCache> findByCategoryName(String categoryName);
}
