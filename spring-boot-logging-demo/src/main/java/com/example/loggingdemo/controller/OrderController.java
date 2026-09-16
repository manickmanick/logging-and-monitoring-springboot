package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final MeterRegistry meterRegistry;

    public OrderController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostMapping
    public String createOrder(
            @RequestParam String orderType) {

        Counter orderCounter = Counter.builder("orders.created")
                .description("Number of orders created")
                .tag("orderType", orderType)
                .register(meterRegistry);

        orderCounter.increment();

        return "Order created with type " + orderType;
    }
}