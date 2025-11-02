package com.company.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private Map<String, Long> counters;
    private long startTime;
    private long elapsedTime;

    public Metrics() {
        this.counters = new HashMap<>();
    }

    public void incrementCounter(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }

    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public long stopTimer() {
        elapsedTime = System.nanoTime() - startTime;
        return elapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void reset() {
        counters.clear();
        startTime = 0;
        elapsedTime = 0;
    }

    public void printMetrics() {
        System.out.println("=== Metrics ===");
        System.out.println("Time: " + (elapsedTime / 1000000.0) + " ms");
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("===============");
    }
}