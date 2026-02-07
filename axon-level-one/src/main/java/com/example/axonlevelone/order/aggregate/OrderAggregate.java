package com.example.axonlevelone.order.aggregate;

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

    protected OrderAggregate() {
        // Axon による Event Sourcing 復元用（引数なしコンストラクタ）
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand: orderId={}, productName={}",
                command.getOrderId(), command.getProductName());
        AggregateLifecycle.apply(OrderCreatedEvent.of(
                command.getOrderId(), command.getProductName()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("Applying OrderCreatedEvent: orderId={}, productName={}",
                event.getOrderId(), event.getProductName());
        this.orderId = event.getOrderId();
        this.productName = event.getProductName();
    }
}
