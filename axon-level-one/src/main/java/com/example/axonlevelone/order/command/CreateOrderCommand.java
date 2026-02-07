package com.example.axonlevelone.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productName;

    public CreateOrderCommand(String orderId, String productName) {
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
