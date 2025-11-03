package graph.dagsp;

import metrics.Metrics;
import metrics.SimpleMetrics;

import java.util.*;

/**
 * Implementation of shortest and longest path algorithms for DAGs.
 * Uses edge weights from the graph.
 */
public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath() {
        this.metrics = new SimpleMetrics();
    }

    /**
     * Compute single-source shortest paths in a DAG.
     *
     * @param graph     adjacency list with weights [target, weight]
     * @param topoOrder topological order of vertices
     * @param source    source vertex
     * @return array of shortest distances (Double.POSITIVE_INFINITY if unreachable)
     */
    public double[] shortestPaths(List<List<double[]>> graph, List<Integer> topoOrder, int source) {
        metrics.reset();
        metrics.startTimer();

        int n = graph.size();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0.0;

        // Process vertices in topological order
        boolean foundSource = false;
        for (int u : topoOrder) {
            if (u == source) {
                foundSource = true;
            }
            if (!foundSource) {
                continue;
            }

            if (dist[u] != Double.POSITIVE_INFINITY) {
                for (double[] edge : graph.get(u)) {
                    int v = (int) edge[0];
                    double w = edge[1];
                    metrics.incrementCounter("relaxations");
                    if (dist[v] > dist[u] + w) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    /**
     * Compute longest paths in a DAG (critical path).
     * Uses max-DP over topological order.
     *
     * @param graph     adjacency list with weights [target, weight]
     * @param topoOrder topological order of vertices
     * @param source    source vertex for longest path
     * @return array of longest distances
     */
    public double[] longestPaths(List<List<double[]>> graph, List<Integer> topoOrder, int source) {
        metrics.reset();
        metrics.startTimer();

        int n = graph.size();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        dist[source] = 0.0;

        // Process vertices in topological order
        boolean foundSource = false;
        for (int u : topoOrder) {
            if (u == source) {
                foundSource = true;
            }
            if (!foundSource) {
                continue;
            }

            if (dist[u] != Double.NEGATIVE_INFINITY) {
                for (double[] edge : graph.get(u)) {
                    int v = (int) edge[0];
                    double w = edge[1];
                    metrics.incrementCounter("relaxations");
                    if (dist[v] < dist[u] + w) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    /**
     * Find the critical path (longest path) and its length.
     *
     * @param graph     adjacency list with weights
     * @param topoOrder topological order
     * @param source    source vertex
     * @return CriticalPathResult containing path and length
     */
    public CriticalPathResult findCriticalPath(List<List<double[]>> graph, List<Integer> topoOrder, int source) {
        double[] dist = longestPaths(graph, topoOrder, source);

        // Find vertex with maximum distance
        int maxVertex = source;
        double maxDist = dist[source];
        for (int i = 0; i < dist.length; i++) {
            if (dist[i] > maxDist && dist[i] != Double.NEGATIVE_INFINITY) {
                maxDist = dist[i];
                maxVertex = i;
            }
        }

        // Reconstruct path from source to maxVertex
        List<Integer> path = reconstructPath(graph, topoOrder, source, maxVertex, dist, true);

        return new CriticalPathResult(path, maxDist);
    }

    /**
     * Reconstruct a path from source to target.
     *
     * @param graph     adjacency list
     * @param topoOrder topological order
     * @param source    source vertex
     * @param target    target vertex
     * @param dist      distance array
     * @param longest   if true, reconstruct longest path; if false, shortest
     * @return path as list of vertex indices
     */
    public List<Integer> reconstructPath(
            List<List<double[]>> graph,
            List<Integer> topoOrder,
            int source,
            int target,
            double[] dist,
            boolean longest) {

        if (longest ? dist[target] == Double.NEGATIVE_INFINITY
                : dist[target] == Double.POSITIVE_INFINITY) {
            return new ArrayList<>(); // No path exists
        }

        // Build parent map by working forward through topological order
        Map<Integer, Integer> parent = new HashMap<>();
        double[] distForward = new double[graph.size()];
        Arrays.fill(distForward, longest ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        distForward[source] = 0.0;

        boolean foundSource = false;
        for (int u : topoOrder) {
            if (u == source) {
                foundSource = true;
            }
            if (!foundSource) {
                continue;
            }

            if (distForward[u] != (longest ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY)) {
                for (double[] edge : graph.get(u)) {
                    int v = (int) edge[0];
                    double w = edge[1];
                    if (longest) {
                        if (distForward[v] < distForward[u] + w) {
                            distForward[v] = distForward[u] + w;
                            parent.put(v, u);
                        }
                    } else {
                        if (distForward[v] > distForward[u] + w) {
                            distForward[v] = distForward[u] + w;
                            parent.put(v, u);
                        }
                    }
                }
            }
        }

        // Reconstruct path backwards
        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != source && parent.containsKey(current)) {
            path.add(0, current);
            current = parent.get(current);
        }
        path.add(0, source);

        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Result class for critical path.
     */
    public static class CriticalPathResult {
        private List<Integer> path;
        private double length;

        public CriticalPathResult(List<Integer> path, double length) {
            this.path = path;
            this.length = length;
        }

        public List<Integer> getPath() {
            return path;
        }

        public double getLength() {
            return length;
        }
    }
}

