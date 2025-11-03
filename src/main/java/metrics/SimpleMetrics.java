package metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of Metrics interface using System.nanoTime().
 */
public class SimpleMetrics implements Metrics {
    private long startTime;
    private long endTime;
    private Map<String, Long> counters;

    public SimpleMetrics() {
        this.counters = new HashMap<>();
        reset();
    }

    @Override
    public void startTimer() {
        startTime = System.nanoTime();
    }

    @Override
    public void stopTimer() {
        endTime = System.nanoTime();
    }

    @Override
    public long getElapsedTime() {
        return endTime - startTime;
    }

    @Override
    public void incrementCounter(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }

    @Override
    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }

    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Time: %.3f ms", getElapsedTime() / 1_000_000.0));
        sb.append("\nCounters:\n");
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}






