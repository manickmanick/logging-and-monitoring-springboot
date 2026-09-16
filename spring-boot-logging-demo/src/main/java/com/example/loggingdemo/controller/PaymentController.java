package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final MeterRegistry meterRegistry;

    public PaymentController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostMapping
    public String processPayment(
            @RequestParam String paymentMethod,
            @RequestParam String paymentStatus) {

        Counter paymentCounter = Counter.builder("payments.processed")
                .description("Number of payments processed")
                .tag("paymentMethod", paymentMethod)
                .tag("paymentStatus", paymentStatus)
                .register(meterRegistry);

        paymentCounter.increment();

        return "Payment processed using "
                + paymentMethod
                + " with status "
                + paymentStatus;
    }

    @PostMapping("/amount")
    public String recordPaymentAmount(
            @RequestParam double amount) {

        DistributionSummary paymentAmount =
                DistributionSummary.builder("payment.amount")
                        .description("Distribution of payment amounts")
                        .register(meterRegistry);

        paymentAmount.record(amount);

        return "Payment amount recorded: " + amount;
    }

    @GetMapping("/percentile")
    public String recordPercentile() {

        Timer paymentTimer = Timer.builder("payment.processing.time")
                .description("Time taken to process payment")
                .publishPercentiles(
                        0.5,
                        0.9,
                        0.95,
                        0.99
                )
                .register(meterRegistry);

        long delay = ThreadLocalRandom.current().nextLong(2000, 5001);

        paymentTimer.record(delay, TimeUnit.MILLISECONDS);

        return "Processing time: " + delay + " ms";
    }
}