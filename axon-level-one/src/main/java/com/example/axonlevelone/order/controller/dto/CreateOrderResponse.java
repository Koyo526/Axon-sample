package com.example.axonlevelone.order.controller.dto;

import com.example.axonlevelone.order.OrderStatus;

public record CreateOrderResponse(String orderId, OrderStatus status) {

    public static CreateOrderResponse created(String orderId) {
        return new CreateOrderResponse(orderId, OrderStatus.CREATED);
    }
}
