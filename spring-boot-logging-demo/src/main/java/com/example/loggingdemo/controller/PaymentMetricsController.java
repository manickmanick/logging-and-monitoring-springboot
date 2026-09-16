package com.example.loggingdemo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/metrics/payments")
public class PaymentMetricsController {

    private final Counter paymentCounter;

    private final AtomicInteger pendingPayments;

    private final Timer paymentTimer;

    private final DistributionSummary paymentAmount;

    public PaymentMetricsController(MeterRegistry meterRegistry) {

        /*
         * 1. COUNTER
         *
         * Counts the total number of payments processed.
         */
        paymentCounter = Counter.builder("payments.processed")
                .description("Total number of payments processed")
                .tag("service", "payment")
                .register(meterRegistry);


        /*
         * 2. GAUGE
         *
         * Represents the current number of pending payments.
         *
         * Gauge reads the current value from AtomicInteger.
         */
        pendingPayments = new AtomicInteger(0);

        Gauge.builder(
                        "payments.pending",
                        pendingPayments,
                        AtomicInteger::get
                )
                .description("Current number of pending payments")
                .tag("service", "payment")
                .register(meterRegistry);


        /*
         * 3. TIMER
         *
         * Measures how long payment processing takes.
         */
        paymentTimer = Timer.builder("payment.processing.time")
                .description("Time taken to process payment")
                .tag("service", "payment")
                .publishPercentiles(
                        0.5,
                        0.9,
                        0.95,
                        0.99
                )
                .register(meterRegistry);


        /*
         * 4. DISTRIBUTION SUMMARY
         *
         * Measures the distribution of payment amounts.
         */
        paymentAmount = DistributionSummary.builder("payment.amount")
                .description("Distribution of payment amounts")
                .tag("service", "payment")
                .register(meterRegistry);
    }


    /*
     * COUNTER API
     *
     * Every request increments the payment counter.
     */
    @PostMapping("/process")
    public String processPayment() {

        paymentCounter.increment();

        return "Payment processed";
    }


    /*
     * GAUGE API
     *
     * Add a pending payment.
     */
    @PostMapping("/pending/add")
    public String addPendingPayment() {

        int currentPending = pendingPayments.incrementAndGet();

        return "Pending payments: " + currentPending;
    }


    /*
     * GAUGE API
     *
     * Complete/remove a pending payment.
     */
    @PostMapping("/pending/remove")
    public String removePendingPayment() {

        int currentPending = pendingPayments.updateAndGet(
                value -> Math.max(0, value - 1)
        );

        return "Pending payments: " + currentPending;
    }


    /*
     * TIMER API
     *
     * Simulates payment processing.
     *
     * Random processing time:
     * 2 seconds → 5 seconds
     */
    @PostMapping("/process-with-timer")
    public String processPaymentWithTimer() {

        Timer.Sample sample = .start();

        try {

            int processingTime =
                    ThreadLocalRandom.current()
                            .nextInt(2000, 5001);

            Thread.sleep(processingTime);Timer

            return "Payment processed in "
                    + processingTime
                    + " ms";

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return "Payment processing interrupted";

        } finally {

            sample.stop(paymentTimer);
        }
    }


    /*
     * DISTRIBUTION SUMMARY API
     *
     * Records a payment amount.
     */
    @PostMapping("/amount")
    public String recordPaymentAmount(
            @RequestParam double amount) {

        paymentAmount.record(amount);

        return "Payment amount recorded: " + amount;
    }
}