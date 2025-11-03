package metrics;

/**
 * Interface for collecting metrics during algorithm execution.
 */
public interface Metrics {
    /**
     * Record the start time of an operation.
     */
    void startTimer();

    /**
     * Record the end time of an operation.
     */
    void stopTimer();

    /**
     * Get the elapsed time in nanoseconds.
     */
    long getElapsedTime();

    /**
     * Increment a counter by name.
     */
    void incrementCounter(String counterName);

    /**
     * Get the value of a counter.
     */
    long getCounter(String counterName);

    /**
     * Reset all metrics.
     */
    void reset();

    /**
     * Get a formatted summary of all metrics.
     */
    String getSummary();
}






