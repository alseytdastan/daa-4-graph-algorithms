package graph.scc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tarjan's SCC algorithm.
 */
public class TarjanSCCTest {

    @Test
    public void testSimpleDAG() {
        // Simple DAG: 0 -> 1 -> 2
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(List.of(1)));
        graph.add(new ArrayList<>(List.of(2)));
        graph.add(new ArrayList<>());

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);

        assertEquals(3, sccs.size()); // Each vertex is its own SCC
    }

    @Test
    public void testSingleCycle() {
        // Cycle: 0 -> 1 -> 2 -> 0
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(List.of(1)));
        graph.add(new ArrayList<>(List.of(2)));
        graph.add(new ArrayList<>(List.of(0)));

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);

        assertEquals(1, sccs.size()); // All vertices in one SCC
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    public void testMultipleSCCs() {
        // Two cycles: 0 <-> 1 and 2 <-> 3, with edge 0 -> 2
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(List.of(1, 2)));
        graph.add(new ArrayList<>(List.of(0)));
        graph.add(new ArrayList<>(List.of(3)));
        graph.add(new ArrayList<>(List.of(2)));

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);

        assertEquals(2, sccs.size()); // Two SCCs
        // Check that each SCC has 2 vertices
        boolean foundSize2 = false;
        for (List<Integer> scc : sccs) {
            if (scc.size() == 2) {
                foundSize2 = true;
            }
        }
        assertTrue(foundSize2);
    }

    @Test
    public void testEmptyGraph() {
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>());
        graph.add(new ArrayList<>());
        graph.add(new ArrayList<>());

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);

        assertEquals(3, sccs.size()); // Each vertex is its own SCC
    }

    @Test
    public void testCondensationGraph() {
        // Cycle: 0 -> 1 -> 0, isolated vertex 2
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>(List.of(1)));
        graph.add(new ArrayList<>(List.of(0)));
        graph.add(new ArrayList<>());

        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(graph);
        List<List<Integer>> condensation = tarjan.buildCondensationGraph(graph, sccs);

        assertEquals(2, condensation.size()); // Two components
        // Component 0 (vertices 0,1) should have no outgoing edges (or edges to component 1)
        // Component 1 (vertex 2) should have no outgoing edges
    }
}
