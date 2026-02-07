package com.example.axonlevelone.order.aggregate;

import com.example.axonlevelone.order.command.CreateOrderCommand;
import com.example.axonlevelone.order.event.OrderCreatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderAggregateTest {

    private FixtureConfiguration<OrderAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    void createOrder_正常にEventが発行される() {
        fixture.givenNoPriorActivity()
                .when(CreateOrderCommand.of("ORD-001", "Coffee"))
                .expectEvents(OrderCreatedEvent.of("ORD-001", "Coffee"));
    }

    @Test
    void createOrder_同じorderNumberで2回目は例外が発生する() {
        fixture.given(OrderCreatedEvent.of("ORD-001", "Coffee"))
                .when(CreateOrderCommand.of("ORD-001", "Coffee"))
                .expectException(Exception.class);
    }
}
