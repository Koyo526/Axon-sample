package com.example.axonlevelone.order.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productName;

    public static CreateOrderCommand of(String orderId, String productName) {
        return new CreateOrderCommand(orderId, productName);
    }
}
