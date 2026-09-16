package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final MeterRegistry meterRegistry;

    public PaymentController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostMapping
    public String processPayment(
            @RequestParam String paymentMethod) {

        Counter paymentCounter = Counter.builder("payments.processed")
                .description("Number of payments processed")
                .tag("paymentMethod", paymentMethod)
                .register(meterRegistry);

        paymentCounter.increment();

        return "Payment processed using " + paymentMethod;
    }
}