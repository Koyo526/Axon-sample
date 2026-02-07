package com.example.axonlevelone.order.controller;

import com.example.axonlevelone.order.controller.dto.OrderSummary;
import com.example.axonlevelone.order.query.GetOrdersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderQueryController {

    private final QueryGateway queryGateway;

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
