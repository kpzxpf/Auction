package com.volzhin.auction.producer;

import com.volzhin.auction.entity.bid.BidCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NewBidProducer extends AbstractProducer<BidCache> implements KafkaProducer<BidCache> {
    @Value("${spring.kafka.topic.names.new-bid}")
    private String topic;

    public NewBidProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void send(BidCache event) {
        super.sendMessage(topic, event);
    }
}
