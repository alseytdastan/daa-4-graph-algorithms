package app;

import graph.dagsp.DAGShortestPath;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import io.GraphLoader;
import model.GraphData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Main class to orchestrate the execution of SCC, Topological Sort, and DAG Shortest Path algorithms.
 */
public class Main {
    public static void main(String[] args) {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Generate datasets if they don't exist
        try {
            if (!new File("data/small_1.json").exists()) {
                System.out.println("Generating datasets...");
                DatasetGenerator.generateAllDatasets("data");
                System.out.println("Datasets generated successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            return;
        }

        // Process each dataset
        String[] datasets = {
                "small_1", "small_2", "small_3",
                "medium_1", "medium_2", "medium_3",
                "large_1", "large_2", "large_3"
        };

        for (String datasetName : datasets) {
            processDataset(datasetName);
        }

        System.out.println("\nAll datasets processed. Results saved in output/ directory.");
    }

    /**
     * Process a single dataset: SCC, Topological Sort, and DAG Shortest Paths.
     */
    private static void processDataset(String datasetName) {
        System.out.println("\n========================================");
        System.out.println("Processing: " + datasetName);
        System.out.println("========================================");

        try {
            // Load graph
            GraphData graphData = GraphLoader.loadGraph("data/" + datasetName + ".json");
            System.out.println("Loaded graph: " + graphData.getN() + " vertices, " +
                    (graphData.getEdges() != null ? graphData.getEdges().size() : 0) + " edges");
            System.out.println("Source vertex: " + graphData.getSource());
            System.out.println("Weight model: " + graphData.getWeightModel());

            // Convert to adjacency list for algorithms
            List<List<Integer>> graph = GraphLoader.createAdjacencyList(graphData);
            List<List<double[]>> weightedGraph = GraphLoader.createAdjacencyListDouble(graphData);

            // 1. Find SCCs using Tarjan
            TarjanSCC tarjan = new TarjanSCC();
            List<List<Integer>> sccs = tarjan.findSCCs(graph);
            System.out.println("\n--- SCC Results ---");
            System.out.println("Number of SCCs: " + sccs.size());
            for (int i = 0; i < sccs.size(); i++) {
                System.out.println("  SCC " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")");
            }
            System.out.println("SCC Metrics: " + tarjan.getMetrics().getSummary());

            // 2. Build condensation graph
            List<List<Integer>> condensation = tarjan.buildCondensationGraph(graph, sccs);
            System.out.println("\n--- Condensation Graph ---");
            System.out.println("Components: " + condensation.size());
            int condensationEdges = condensation.stream().mapToInt(List::size).sum();
            System.out.println("Edges: " + condensationEdges);

            // 3. Topological sort on condensation graph
            TopologicalSort topo = new TopologicalSort();
            List<Integer> topoOrder = topo.topologicalSort(condensation);
            List<Integer> vertexOrder = null;
            if (topoOrder != null) {
                System.out.println("\n--- Topological Order (Components) ---");
                System.out.println("Order: " + topoOrder);
                vertexOrder = topo.deriveVertexOrder(topoOrder, sccs);
                System.out.println("Derived Vertex Order: " + vertexOrder);
                System.out.println("Topo Metrics: " + topo.getMetrics().getSummary());
            } else {
                System.out.println("\n--- Topological Sort Failed ---");
                System.out.println("Graph contains cycles (should not happen for condensation graph)");
            }

            // 4. DAG Shortest/Longest Paths
            DAGShortestPath dagsp = new DAGShortestPath();
            if (topoOrder != null && vertexOrder != null) {
                // Shortest paths from source (from JSON)
                int source = graphData.getSource();
                System.out.println("\n--- DAG Shortest Paths ---");
                    double[] shortestDist = dagsp.shortestPaths(weightedGraph, vertexOrder, source);
                    System.out.println("Shortest distances from source " + source + ":");
                    for (int i = 0; i < Math.min(10, shortestDist.length); i++) {
                        if (shortestDist[i] != Double.POSITIVE_INFINITY) {
                            System.out.printf("  %d: %.2f\n", i, shortestDist[i]);
                        }
                    }
                    System.out.println("Shortest Path Metrics: " + dagsp.getMetrics().getSummary());

                // Longest path (critical path)
                System.out.println("\n--- Critical Path (Longest) ---");
                DAGShortestPath.CriticalPathResult criticalPath = dagsp.findCriticalPath(weightedGraph, vertexOrder, source);
                System.out.println("Critical path length: " + criticalPath.getLength());
                System.out.println("Critical path: " + criticalPath.getPath());
            }

            // Write results to output file
            writeResults(datasetName, graphData, sccs, topoOrder, vertexOrder, weightedGraph, tarjan, topo, dagsp);

        } catch (IOException e) {
            System.err.println("Error processing " + datasetName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Write results to output file.
     */
    private static void writeResults(
            String datasetName,
            GraphData graphData,
            List<List<Integer>> sccs,
            List<Integer> topoOrder,
            List<Integer> vertexOrder,
            List<List<double[]>> weightedGraph,
            TarjanSCC tarjan,
            TopologicalSort topo,
            DAGShortestPath dagsp) throws IOException {

        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File("output/" + datasetName);
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Dataset: " + datasetName + "\n");
            writer.write("Vertices: " + graphData.getN() + "\n");
            writer.write("Edges: " + (graphData.getEdges() != null ? graphData.getEdges().size() : 0) + "\n");
            writer.write("Source: " + graphData.getSource() + "\n");
            writer.write("Weight Model: " + graphData.getWeightModel() + "\n\n");

            writer.write("=== SCC Results ===\n");
            writer.write("Number of SCCs: " + sccs.size() + "\n");
            for (int i = 0; i < sccs.size(); i++) {
                writer.write("SCC " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")\n");
            }
            writer.write("\nSCC Metrics:\n");
            writer.write(tarjan.getMetrics().getSummary());
            writer.write("\n");

            if (topoOrder != null) {
                writer.write("\n=== Topological Order ===\n");
                writer.write("Component Order: " + topoOrder + "\n");
                writer.write("\nTopo Metrics:\n");
                writer.write(topo.getMetrics().getSummary());
                writer.write("\n");
            }

            writer.write("\n=== DAG Shortest Path Results ===\n");
            writer.write("Source: " + graphData.getSource() + "\n");
            if (topoOrder != null && vertexOrder != null) {
                int source = graphData.getSource();
                double[] shortestDist = dagsp.shortestPaths(weightedGraph, vertexOrder, source);
                writer.write("Shortest distances from source " + source + ":\n");
                int reachableCount = 0;
                for (int i = 0; i < shortestDist.length; i++) {
                    if (shortestDist[i] != Double.POSITIVE_INFINITY) {
                        writer.write(String.format("  Vertex %d: %.2f\n", i, shortestDist[i]));
                        reachableCount++;
                    }
                }
                writer.write("Reachable vertices: " + reachableCount + "/" + shortestDist.length + "\n");
                
                // Critical path
                DAGShortestPath.CriticalPathResult criticalPath = dagsp.findCriticalPath(weightedGraph, vertexOrder, source);
                writer.write("\nCritical Path (Longest):\n");
                writer.write("  Length: " + criticalPath.getLength() + "\n");
                writer.write("  Path: " + criticalPath.getPath() + "\n");
            }
            writer.write("\n=== DAG Shortest Path Metrics ===\n");
            writer.write(dagsp.getMetrics().getSummary());
            writer.write("\n");
        }
    }
}
