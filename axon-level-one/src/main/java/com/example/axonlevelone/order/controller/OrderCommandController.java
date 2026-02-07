package com.example.axonlevelone.order.controller;

import com.example.axonlevelone.order.command.CreateOrderCommand;
import com.example.axonlevelone.order.controller.dto.CreateOrderRequest;
import com.example.axonlevelone.order.controller.dto.CreateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        String orderNumber = request.orderNumber();
        log.info("[1] Received POST /orders: orderNumber={}, productName={}", orderNumber, request.productName());
        log.info("[2] Sending CreateOrderCommand: orderNumber={}", orderNumber);

        try {
            commandGateway.sendAndWait(CreateOrderCommand.of(orderNumber, request.productName()));

            CreateOrderResponse response = CreateOrderResponse.created(orderNumber);
            log.info("[5] Order created successfully: orderNumber={}, status={}", response.orderNumber(), response.status());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CommandExecutionException e) {
            log.info("[5] Duplicate order detected: orderNumber={}", orderNumber);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CreateOrderResponse.alreadyProcessed(orderNumber));
        }
    }
}
