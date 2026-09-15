package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final AtomicInteger pendingOrders = new AtomicInteger(0);

    public OrderController(MeterRegistry meterRegistry) {

        Gauge.builder(
                        "orders.pending",
                        pendingOrders,
                        AtomicInteger::get
                )
                .description("Number of pending orders")
                .register(meterRegistry);
    }

    @PostMapping
    public String createOrder() {

        pendingOrders.incrementAndGet();

        return "Order created";
    }

    @PostMapping("/complete")
    public String completeOrder() {

        pendingOrders.decrementAndGet();

        return "Order completed";
    }
}