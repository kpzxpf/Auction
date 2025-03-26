package com.volzhin.auction.consumer;

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}
