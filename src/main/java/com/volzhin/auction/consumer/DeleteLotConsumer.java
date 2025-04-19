package com.volzhin.auction.consumer;

import com.volzhin.auction.dto.event.DeleteLotEvent;
import com.volzhin.auction.service.cache.LotCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteLotConsumer extends AbstractConsumer<DeleteLotEvent> implements KafkaConsumer<DeleteLotEvent> {
    private final LotCacheService lotCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.names.delete-lot}",
            groupId = "${spring.kafka.consumer.group-id.lot-group}")
    public void listen(DeleteLotEvent deleteLotEvent, Acknowledgment acknowledgment) {
        log.info("New event received: {}", deleteLotEvent);

        super.handle(deleteLotEvent, event -> {
            if (lotCacheService.existsLotById(event.getId())) {
                lotCacheService.deleteLot(event.getId());
            }
        });

        acknowledgment.acknowledge();
    }
}
