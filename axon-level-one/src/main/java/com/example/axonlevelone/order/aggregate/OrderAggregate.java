package com.example.axonlevelone.order.aggregate;

import com.example.axonlevelone.order.OrderStatus;
import com.example.axonlevelone.order.command.CreateOrderCommand;
import com.example.axonlevelone.order.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productName;
    private OrderStatus status;

    protected OrderAggregate() {
        // Axon による Event Sourcing 復元用（引数なしコンストラクタ）
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("[3] Command received: orderId={}, productName={}",
                command.getOrderId(), command.getProductName());

        if (command.getProductName() == null || command.getProductName().isBlank()) {
            throw new IllegalArgumentException("商品名は必須です");
        }

        log.info("[3] Publishing OrderCreatedEvent");
        AggregateLifecycle.apply(OrderCreatedEvent.of(
                command.getOrderId(), command.getProductName()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.productName = event.getProductName();
        this.status = OrderStatus.CREATED;
        log.info("[4] Event applied, aggregate state updated: orderId={}, productName={}, status={}",
                this.orderId, this.productName, this.status);
    }
}
