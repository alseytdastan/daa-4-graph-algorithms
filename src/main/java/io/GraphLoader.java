package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.EdgeData;
import model.GraphData;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving graph data from/to JSON files.
 * Uses the format with directed, n, edges (u/v/w), source, and weight_model fields.
 */
public class GraphLoader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Load graph data from a JSON file.
     *
     * @param filePath path to the JSON file
     * @return GraphData object
     * @throws IOException if file cannot be read
     */
    public static GraphData loadGraph(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GraphData.class);
        }
    }

    /**
     * Save graph data to a JSON file.
     *
     * @param graphData the graph data to save
     * @param filePath  path to save the JSON file
     * @throws IOException if file cannot be written
     */
    public static void saveGraph(GraphData graphData, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(graphData, writer);
        }
    }

    /**
     * Create an adjacency list representation from GraphData (for SCC and Topo).
     *
     * @param graphData the graph data
     * @return adjacency list (list of neighbor vertices)
     */
    public static List<List<Integer>> createAdjacencyList(GraphData graphData) {
        int n = graphData.getN();
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        if (graphData.getEdges() != null) {
            for (EdgeData edge : graphData.getEdges()) {
                adj.get(edge.getU()).add(edge.getV());
            }
        }

        return adj;
    }

    /**
     * Create an adjacency list with edge weights as doubles.
     *
     * @param graphData the graph data
     * @return adjacency list with [target, weight] pairs
     */
    public static List<List<double[]>> createAdjacencyListDouble(GraphData graphData) {
        int n = graphData.getN();
        List<List<double[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        if (graphData.getEdges() != null) {
            for (EdgeData edge : graphData.getEdges()) {
                adj.get(edge.getU()).add(new double[]{edge.getV(), edge.getW()});
            }
        }

        return adj;
    }
}
