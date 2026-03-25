package com.example.axonlevelone.order.projection;

import com.example.axonlevelone.order.OrderStatus;
import com.example.axonlevelone.order.controller.dto.OrderSummary;
import com.example.axonlevelone.order.event.OrderCreatedEvent;
import com.example.axonlevelone.order.query.GetOrdersQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class OrderProjection {

    // EventHandler と QueryHandler は異なるスレッドから呼ばれる可能性があるため、
    // スレッドセーフな CopyOnWriteArrayList を使用する
    private final List<OrderSummary> orders = new CopyOnWriteArrayList<>();

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderSummary orderSummary = OrderSummary.create(
                event.getOrderId(), event.getProductName(), OrderStatus.CREATED);
        orders.add(orderSummary);
        log.info("OrderCreatedEvent handled: orderId={}, productName={}", event.getOrderId(), event.getProductName());
    }

    @QueryHandler
    public List<OrderSummary> handle(GetOrdersQuery query) {
        log.info("Handling GetOrdersQuery: returning {} orders", orders.size());
        return List.copyOf(orders);
    }
}
