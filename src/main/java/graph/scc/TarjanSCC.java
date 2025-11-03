package graph.scc;

import metrics.Metrics;
import metrics.SimpleMetrics;

import java.util.*;

/**
 * Implementation of Tarjan's algorithm for finding Strongly Connected Components (SCC).
 */
public class TarjanSCC {
    private Metrics metrics;
    private List<List<Integer>> graph;
    private int[] index;
    private int[] lowlink;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int currentIndex;
    private List<List<Integer>> components;

    public TarjanSCC() {
        this.metrics = new SimpleMetrics();
    }

    /**
     * Find all strongly connected components in the graph.
     *
     * @param graph adjacency list representation
     * @return list of SCCs, where each SCC is a list of vertex indices
     */
    public List<List<Integer>> findSCCs(List<List<Integer>> graph) {
        metrics.reset();
        metrics.startTimer();

        this.graph = graph;
        int n = graph.size();
        this.index = new int[n];
        this.lowlink = new int[n];
        this.onStack = new boolean[n];
        this.stack = new Stack<>();
        this.currentIndex = 0;
        this.components = new ArrayList<>();

        Arrays.fill(index, -1);

        for (int i = 0; i < n; i++) {
            if (index[i] == -1) {
                metrics.incrementCounter("DFS_visits");
                strongConnect(i);
            }
        }

        metrics.stopTimer();
        return components;
    }

    /**
     * Recursive DFS for Tarjan's algorithm.
     */
    private void strongConnect(int v) {
        index[v] = currentIndex;
        lowlink[v] = currentIndex;
        currentIndex++;
        stack.push(v);
        onStack[v] = true;
        metrics.incrementCounter("DFS_visits");

        for (int w : graph.get(v)) {
            metrics.incrementCounter("edge_explorations");
            if (index[w] == -1) {
                strongConnect(w);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);
            } else if (onStack[w]) {
                lowlink[v] = Math.min(lowlink[v], index[w]);
            }
        }

        if (lowlink[v] == index[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            components.add(component);
        }
    }

    /**
     * Build the condensation graph (DAG of SCCs).
     *
     * @param originalGraph original adjacency list
     * @param sccs          list of strongly connected components
     * @return condensation graph as adjacency list (component indices)
     */
    public List<List<Integer>> buildCondensationGraph(
            List<List<Integer>> originalGraph,
            List<List<Integer>> sccs) {

        metrics.startTimer();
        int n = sccs.size();

        // Map each vertex to its component index
        int[] vertexToComponent = new int[originalGraph.size()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToComponent[vertex] = i;
            }
        }

        // Build condensation graph
        List<List<Integer>> condensation = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>();

        for (int i = 0; i < n; i++) {
            condensation.add(new ArrayList<>());
        }

        for (int i = 0; i < originalGraph.size(); i++) {
            int fromComponent = vertexToComponent[i];
            for (int neighbor : originalGraph.get(i)) {
                int toComponent = vertexToComponent[neighbor];
                if (fromComponent != toComponent) {
                    String edgeKey = fromComponent + "," + toComponent;
                    if (!edgeSet.contains(edgeKey)) {
                        edgeSet.add(edgeKey);
                        condensation.get(fromComponent).add(toComponent);
                        metrics.incrementCounter("condensation_edges");
                    }
                }
            }
        }

        metrics.stopTimer();
        return condensation;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}



