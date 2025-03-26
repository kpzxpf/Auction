package com.volzhin.auction.consumer;

import com.volzhin.auction.entity.bid.BidCache;
import com.volzhin.auction.service.cache.BidCacheService;
import com.volzhin.auction.service.cache.LotCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewBidConsumer extends AbstractConsumer<BidCache> implements KafkaConsumer<BidCache> {
    private final BidCacheService bidCacheService;
    private final LotCacheService lotCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.names.update-lot}",
            groupId = "${spring.kafka.consumer.group-id.lot-group}")
    public void listen(BidCache bidCache) {
        log.info("New event received: {}", bidCache);

        super.handle(bidCache, event -> {
            if (lotCacheService.existsLotById(event.getId())) {
                bidCacheService.saveBid(bidCache);
            }
        });
    }
}