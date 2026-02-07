package com.example.axonlevelone.order.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedEvent {

    private final String orderId;
    private final String productName;

    public static OrderCreatedEvent of(String orderId, String productName) {
        return new OrderCreatedEvent(orderId, productName);
    }
}
