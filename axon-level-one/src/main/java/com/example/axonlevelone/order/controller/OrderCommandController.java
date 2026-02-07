package com.example.axonlevelone.order.controller;

import com.example.axonlevelone.order.command.CreateOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class OrderCommandController {

    private final CommandGateway commandGateway;

    public OrderCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/orders")
    public Map<String, String> createOrder(@RequestBody Map<String, String> request) {
        String orderId = UUID.randomUUID().toString();
        String productName = request.get("productName");

        commandGateway.sendAndWait(new CreateOrderCommand(orderId, productName));

        return Map.of(
                "orderId", orderId,
                "status", "CREATED"
        );
    }
}
