package com.volzhin.auction.producer;

import com.volzhin.auction.entity.lot.LotCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateLotProducer extends AbstractProducer<LotCache> implements KafkaProducer<LotCache> {
    @Value("${spring.kafka.topic.names.update-lot}")
    private String topic;

    public UpdateLotProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void send(LotCache event) {
        super.sendMessage(topic, event);
    }
}
