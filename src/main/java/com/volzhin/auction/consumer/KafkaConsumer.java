package com.volzhin.auction.consumer;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T> {
    void listen(T event, Acknowledgment acknowledgment);
}
