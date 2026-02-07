package com.example.axonlevelone.config;

import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public InMemoryEventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }
}
