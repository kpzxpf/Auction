package com.volzhin.auction.consumer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractConsumer<T> {

    protected void handle(T event, EventHandler<T> eventHandler) {
        eventHandler.handle(event);
    }
}
