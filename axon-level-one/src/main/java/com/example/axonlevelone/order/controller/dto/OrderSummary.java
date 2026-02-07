package com.example.axonlevelone.order.controller.dto;

public record OrderSummary(String orderId, String productName) {
    public static OrderSummary create(String orderId, String productName) {
        return new OrderSummary(orderId, productName);
    }
}
