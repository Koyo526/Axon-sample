package com.example.axonlevelone.order.controller;

import com.example.axonlevelone.order.command.CreateOrderCommand;
import com.example.axonlevelone.order.controller.dto.CreateOrderRequest;
import com.example.axonlevelone.order.controller.dto.CreateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/orders")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        log.info("[1] Received POST /orders: productName={}", request.productName());

        String orderId = UUID.randomUUID().toString();
        log.info("[2] Sending CreateOrderCommand: orderId={}", orderId);

        commandGateway.sendAndWait(CreateOrderCommand.of(orderId, request.productName()));

        CreateOrderResponse response = CreateOrderResponse.created(orderId);
        log.info("[5] Order created successfully: orderId={}, status={}", response.orderId(), response.status());
        return response;
    }
}
