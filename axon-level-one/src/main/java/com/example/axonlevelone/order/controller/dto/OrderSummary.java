package com.example.axonlevelone.order.controller.dto;

import com.example.axonlevelone.order.OrderStatus;

public record OrderSummary(String orderId, String productName, OrderStatus status) {
    public static OrderSummary create(String orderId, String productName, OrderStatus status) {
        return new OrderSummary(orderId, productName, status);
    }
}
