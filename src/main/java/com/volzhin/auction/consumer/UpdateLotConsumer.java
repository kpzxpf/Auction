package com.volzhin.auction.consumer;

import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.service.cache.LotCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateLotConsumer extends AbstractConsumer<LotCache> implements KafkaConsumer<LotCache> {

    private final LotCacheService lotCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.names.update-lot}",
            groupId = "${spring.kafka.consumer.group-id.lot-group}")
    public void listen(LotCache lotCache) {
        log.info("New event received: {}", lotCache);

        super.handle(lotCache, event -> {
            lotCacheService.updateLot(lotCache);
        });
    }
}
