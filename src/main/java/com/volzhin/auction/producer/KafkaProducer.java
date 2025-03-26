package com.volzhin.auction.producer;

public interface KafkaProducer<T> {
    void send(T event);
}