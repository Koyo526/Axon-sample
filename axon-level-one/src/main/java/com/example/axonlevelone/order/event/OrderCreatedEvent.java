package com.example.axonlevelone.order.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedEvent {

    private final String orderNumber;
    private final String productName;

    public static OrderCreatedEvent of(String orderNumber, String productName) {
        return new OrderCreatedEvent(orderNumber, productName);
    }
}
