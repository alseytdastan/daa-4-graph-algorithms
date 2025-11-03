package app;

import io.GraphLoader;
import model.EdgeData;
import model.GraphData;

import java.io.IOException;
import java.util.*;

/**
 * Generates test datasets with different graph structures and sizes.
 * Generates datasets in the JSON format with directed, n, edges (u/v/w), source, and weight_model fields.
 */
public class DatasetGenerator {
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    /**
     * Generate a graph dataset.
     *
     * @param vertices     number of vertices
     * @param density      edge density (0.0 to 1.0)
     * @param cyclic       whether to include cycles
     * @param multipleSCC whether to include multiple SCCs
     * @param source       source vertex for pathfinding
     * @return GraphData object
     */
    public static GraphData generateDataset(
            int vertices,
            double density,
            boolean cyclic,
            boolean multipleSCC,
            int source) {

        List<EdgeData> edges = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>();

        // Generate base DAG structure
        if (multipleSCC) {
            generateMultipleSCCs(vertices, density, edges, edgeSet);
        } else if (cyclic) {
            generateCyclicGraph(vertices, density, edges, edgeSet);
        } else {
            generateDAG(vertices, density, edges, edgeSet);
        }

        return new GraphData(true, vertices, edges, source, "edge");
    }

    /**
     * Generate a pure DAG.
     */
    private static void generateDAG(int vertices, double density, List<EdgeData> edges, Set<String> edgeSet) {
        int maxEdges = (int) (vertices * (vertices - 1) / 2 * density);
        int edgesGenerated = 0;

        // Generate edges only from lower-indexed to higher-indexed vertices
        for (int i = 0; i < vertices && edgesGenerated < maxEdges; i++) {
            for (int j = i + 1; j < vertices && edgesGenerated < maxEdges; j++) {
                if (random.nextDouble() < density) {
                    String edgeKey = i + "," + j;
                    if (!edgeSet.contains(edgeKey)) {
                        double weight = random.nextDouble() * 10.0 + 1.0;
                        edges.add(new EdgeData(i, j, weight));
                        edgeSet.add(edgeKey);
                        edgesGenerated++;
                    }
                }
            }
        }
    }

    /**
     * Generate a graph with cycles.
     */
    private static void generateCyclicGraph(int vertices, double density, List<EdgeData> edges, Set<String> edgeSet) {
        // Start with DAG structure
        generateDAG(vertices, density * 0.7, edges, edgeSet);

        // Add some backward edges to create cycles
        int cycleEdges = (int) (vertices * density * 0.3);
        for (int i = 0; i < cycleEdges; i++) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);
            if (from != to && !edgeSet.contains(from + "," + to)) {
                double weight = random.nextDouble() * 10.0 + 1.0;
                edges.add(new EdgeData(from, to, weight));
                edgeSet.add(from + "," + to);
            }
        }
    }

    /**
     * Generate a graph with multiple SCCs.
     */
    private static void generateMultipleSCCs(int vertices, double density, List<EdgeData> edges, Set<String> edgeSet) {
        // Create 2-3 separate SCC clusters
        int numClusters = random.nextInt(2) + 2; // 2 or 3 clusters
        int verticesPerCluster = vertices / numClusters;

        for (int cluster = 0; cluster < numClusters; cluster++) {
            int start = cluster * verticesPerCluster;
            int end = (cluster == numClusters - 1) ? vertices : (cluster + 1) * verticesPerCluster;

            // Create edges within cluster (can be cyclic)
            for (int i = start; i < end; i++) {
                for (int j = start; j < end; j++) {
                    if (i != j && random.nextDouble() < density) {
                        String edgeKey = i + "," + j;
                        if (!edgeSet.contains(edgeKey)) {
                            double weight = random.nextDouble() * 10.0 + 1.0;
                            edges.add(new EdgeData(i, j, weight));
                            edgeSet.add(edgeKey);
                        }
                    }
                }
            }
        }

        // Add some inter-cluster edges (from lower cluster to higher)
        for (int cluster = 0; cluster < numClusters - 1; cluster++) {
            int start1 = cluster * verticesPerCluster;
            int end1 = (cluster + 1) * verticesPerCluster;
            int start2 = end1;
            int end2 = (cluster == numClusters - 2) ? vertices : (cluster + 2) * verticesPerCluster;

            for (int i = start1; i < end1; i++) {
                for (int j = start2; j < end2; j++) {
                    if (random.nextDouble() < density * 0.3) {
                        String edgeKey = i + "," + j;
                        if (!edgeSet.contains(edgeKey)) {
                            double weight = random.nextDouble() * 10.0 + 1.0;
                            edges.add(new EdgeData(i, j, weight));
                            edgeSet.add(edgeKey);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate all required datasets.
     */
    public static void generateAllDatasets(String outputDir) throws IOException {
        // Small datasets (6-10 vertices)
        GraphLoader.saveGraph(generateDataset(8, 0.3, false, false, 0), outputDir + "/small_1.json");
        GraphLoader.saveGraph(generateDataset(10, 0.4, true, false, 0), outputDir + "/small_2.json");
        GraphLoader.saveGraph(generateDataset(7, 0.35, false, true, 0), outputDir + "/small_3.json");

        // Medium datasets (10-20 vertices)
        GraphLoader.saveGraph(generateDataset(15, 0.25, false, false, 0), outputDir + "/medium_1.json");
        GraphLoader.saveGraph(generateDataset(18, 0.3, true, true, 2), outputDir + "/medium_2.json");
        GraphLoader.saveGraph(generateDataset(12, 0.35, true, false, 1), outputDir + "/medium_3.json");

        // Large datasets (20-50 vertices)
        GraphLoader.saveGraph(generateDataset(30, 0.2, false, false, 0), outputDir + "/large_1.json");
        GraphLoader.saveGraph(generateDataset(40, 0.25, true, true, 5), outputDir + "/large_2.json");
        GraphLoader.saveGraph(generateDataset(25, 0.3, true, false, 3), outputDir + "/large_3.json");
    }

    /**
     * Main method to generate datasets from command line.
     */
    public static void main(String[] args) {
        String outputDir = args.length > 0 ? args[0] : "data";
        try {
            System.out.println("Generating datasets in: " + outputDir);
            generateAllDatasets(outputDir);
            System.out.println("Datasets generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
