package com.example.axonlevelone.order.event;

public class OrderCreatedEvent {

    private final String orderId;
    private final String productName;

    public OrderCreatedEvent(String orderId, String productName) {
        this.orderId = orderId;
        this.productName = productName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }
}
