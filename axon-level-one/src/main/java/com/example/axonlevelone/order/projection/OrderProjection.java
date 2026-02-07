package com.example.axonlevelone.order.projection;

import com.example.axonlevelone.order.controller.dto.OrderSummary;
import com.example.axonlevelone.order.event.OrderCreatedEvent;
import com.example.axonlevelone.order.query.GetOrdersQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OrderProjection {

    private final List<OrderSummary> orders = new ArrayList<>();

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderSummary orderSummary = OrderSummary.create(event.getOrderNumber(), event.getProductName());
        orders.add(orderSummary);
        log.info("OrderCreatedEvent handled: orderNumber={}, productName={}", event.getOrderNumber(), event.getProductName());
    }

    @QueryHandler
    public List<OrderSummary> handle(GetOrdersQuery query) {
        log.info("Handling GetOrdersQuery: returning {} orders", orders.size());
        return List.copyOf(orders);
    }
}
