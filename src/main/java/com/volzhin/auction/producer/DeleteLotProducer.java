package com.volzhin.auction.producer;

import com.volzhin.auction.dto.event.DeleteLotEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteLotProducer extends AbstractProducer<DeleteLotEvent> implements KafkaProducer<DeleteLotEvent> {
    @Value("${spring.kafka.topic.names.delete-lot}")
    private String topic;

    public DeleteLotProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void send(DeleteLotEvent event) {
        super.sendMessage(topic, event);
    }
}
