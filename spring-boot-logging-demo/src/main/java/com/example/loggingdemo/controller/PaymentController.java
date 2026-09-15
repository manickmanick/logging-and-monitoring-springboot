package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final Timer paymentTimer;

    public PaymentController(MeterRegistry meterRegistry) {

        paymentTimer = Timer.builder("payment.processing.time")
                .description("Time taken to process payment")
                .register(meterRegistry);
    }

    @PostMapping
    public String processPayment() {

        paymentTimer.record(() -> {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

        });

        return "Payment processed";
    }
}