package graph.dagsp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DAG Shortest Path algorithm.
 */
public class DAGShortestPathTest {

    @Test
    public void testShortestPathSimple() {
        // Simple DAG: 0 -> 1 (weight 5), 1 -> 2 (weight 3), 0 -> 2 (weight 10)
        List<List<double[]>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(Arrays.asList(new double[]{1, 5.0}, new double[]{2, 10.0})));
        graph.add(new ArrayList<>(Arrays.asList(new double[]{2, 3.0})));
        graph.add(new ArrayList<>());

        List<Integer> topoOrder = Arrays.asList(0, 1, 2);
        DAGShortestPath dagsp = new DAGShortestPath();
        double[] dist = dagsp.shortestPaths(graph, topoOrder, 0);

        assertEquals(0.0, dist[0], 0.001);
        assertEquals(5.0, dist[1], 0.001);
        assertEquals(8.0, dist[2], 0.001); // 0->1->2 = 5+3 = 8
    }

    @Test
    public void testLongestPath() {
        // DAG: 0 -> 1 (weight 5), 1 -> 2 (weight 3), 0 -> 2 (weight 10)
        List<List<double[]>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(Arrays.asList(new double[]{1, 5.0}, new double[]{2, 10.0})));
        graph.add(new ArrayList<>(Arrays.asList(new double[]{2, 3.0})));
        graph.add(new ArrayList<>());

        List<Integer> topoOrder = Arrays.asList(0, 1, 2);
        DAGShortestPath dagsp = new DAGShortestPath();
        double[] dist = dagsp.longestPaths(graph, topoOrder, 0);

        assertEquals(0.0, dist[0], 0.001);
        assertEquals(5.0, dist[1], 0.001);
        assertEquals(10.0, dist[2], 0.001); // Longest: 0->2 = 10
    }

    @Test
    public void testCriticalPath() {
        // DAG: 0 -> 1 (weight 2), 1 -> 3 (weight 4), 0 -> 2 (weight 3), 2 -> 3 (weight 5)
        // Critical path: 0 -> 2 -> 3 = 8
        List<List<double[]>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(Arrays.asList(new double[]{1, 2.0}, new double[]{2, 3.0})));
        graph.add(new ArrayList<>(Arrays.asList(new double[]{3, 4.0})));
        graph.add(new ArrayList<>(Arrays.asList(new double[]{3, 5.0})));
        graph.add(new ArrayList<>());

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        DAGShortestPath dagsp = new DAGShortestPath();
        DAGShortestPath.CriticalPathResult result = dagsp.findCriticalPath(graph, topoOrder, 0);

        assertEquals(8.0, result.getLength(), 0.001); // 3 + 5 = 8
        assertTrue(result.getPath().contains(0));
        assertTrue(result.getPath().contains(3));
    }

    @Test
    public void testUnreachableVertices() {
        // DAG: 0 -> 1, 2 is isolated
        List<List<double[]>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(Arrays.asList(new double[]{1, 1.0})));
        graph.add(new ArrayList<>());
        graph.add(new ArrayList<>());

        List<Integer> topoOrder = Arrays.asList(0, 1, 2);
        DAGShortestPath dagsp = new DAGShortestPath();
        double[] dist = dagsp.shortestPaths(graph, topoOrder, 0);

        assertEquals(0.0, dist[0], 0.001);
        assertEquals(1.0, dist[1], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, dist[2]);
    }
}
