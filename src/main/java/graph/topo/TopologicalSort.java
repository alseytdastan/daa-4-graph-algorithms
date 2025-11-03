package graph.topo;

import metrics.Metrics;
import metrics.SimpleMetrics;

import java.util.*;

/**
 * Implementation of Kahn's algorithm for topological sorting.
 */
public class TopologicalSort {
    private Metrics metrics;

    public TopologicalSort() {
        this.metrics = new SimpleMetrics();
    }

    /**
     * Perform topological sort using Kahn's algorithm.
     *
     * @param graph adjacency list representation
     * @return topological order, or null if graph contains cycles
     */
    public List<Integer> topologicalSort(List<List<Integer>> graph) {
        metrics.reset();
        metrics.startTimer();

        int n = graph.size();
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (int i = 0; i < n; i++) {
            for (int neighbor : graph.get(i)) {
                inDegree[neighbor]++;
            }
        }

        // Queue for vertices with no incoming edges
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementCounter("pushes");
            }
        }

        List<Integer> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("pops");
            result.add(u);

            for (int v : graph.get(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementCounter("pushes");
                }
            }
        }

        // Check if all vertices were processed (no cycles)
        if (result.size() != n) {
            metrics.stopTimer();
            return null; // Cycle detected
        }

        metrics.stopTimer();
        return result;
    }

    /**
     * Get topological order of original vertices based on component ordering.
     *
     * @param componentOrder topological order of components
     * @param sccs            list of strongly connected components
     * @return topological order of original vertices
     */
    public List<Integer> deriveVertexOrder(List<Integer> componentOrder, List<List<Integer>> sccs) {
        List<Integer> vertexOrder = new ArrayList<>();
        for (int compIdx : componentOrder) {
            vertexOrder.addAll(sccs.get(compIdx));
        }
        return vertexOrder;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}

