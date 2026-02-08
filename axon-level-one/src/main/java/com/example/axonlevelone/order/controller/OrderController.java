package com.example.axonlevelone.order.controller;

import com.example.axonlevelone.order.command.CreateOrderCommand;
import com.example.axonlevelone.order.controller.dto.CreateOrderRequest;
import com.example.axonlevelone.order.controller.dto.CreateOrderResponse;
import com.example.axonlevelone.order.controller.dto.OrderSummary;
import com.example.axonlevelone.order.query.GetOrdersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        log.info("[1] Received POST /order: productName={}", request.productName());

        String orderId = UUID.randomUUID().toString();
        log.info("[2] Sending CreateOrderCommand: orderId={}", orderId);

        commandGateway.sendAndWait(CreateOrderCommand.of(orderId, request.productName()));

        CreateOrderResponse response = CreateOrderResponse.created(orderId);
        log.info("[5] Order created successfully: orderId={}, status={}", response.orderId(), response.status());
        return response;
    }

    @GetMapping("/orders")
    public List<OrderSummary> getOrders() {
        log.info("Received GET /orders");
        List<OrderSummary> orders = queryGateway
                .query(new GetOrdersQuery(), ResponseTypes.multipleInstancesOf(OrderSummary.class))
                .join();
        log.info("Returning {} orders", orders.size());
        return orders;
    }
}
