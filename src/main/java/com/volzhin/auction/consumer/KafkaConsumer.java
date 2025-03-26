package com.volzhin.auction.consumer;

public interface KafkaConsumer<T> {
    void listen(T event);
}
