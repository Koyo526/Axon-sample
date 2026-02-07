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
    private String orderNumber;
    private String productName;

    protected OrderAggregate() {
        // Axon による Event Sourcing 復元用（引数なしコンストラクタ）
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("[3] Command received, publishing OrderCreatedEvent: orderNumber={}, productName={}",
                command.getOrderNumber(), command.getProductName());
        AggregateLifecycle.apply(OrderCreatedEvent.of(
                command.getOrderNumber(), command.getProductName()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderNumber = event.getOrderNumber();
        this.productName = event.getProductName();
        log.info("[4] Event applied, aggregate state updated: orderNumber={}, productName={}",
                this.orderNumber, this.productName);
    }
}
