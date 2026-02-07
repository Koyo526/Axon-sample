package com.example.axonlevelone.order.controller.dto;

public record OrderSummary(String orderNumber, String productName) {
    public static OrderSummary create(String orderNumber, String productName) {
        return new OrderSummary(orderNumber, productName);
    }
}
