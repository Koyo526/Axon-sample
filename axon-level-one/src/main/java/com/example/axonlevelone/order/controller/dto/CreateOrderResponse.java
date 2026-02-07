package com.example.axonlevelone.order.controller.dto;

public record CreateOrderResponse(String orderNumber, OrderStatus status) {

    public static CreateOrderResponse created(String orderNumber) {
        return new CreateOrderResponse(orderNumber, OrderStatus.CREATED);
    }

    public static CreateOrderResponse alreadyProcessed(String orderNumber) {
        return new CreateOrderResponse(orderNumber, OrderStatus.ALREADY_PROCESSED);
    }
}
