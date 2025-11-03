package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.EdgeData;
import model.GraphData;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Utility class to convert between graph formats.
 */
public class GraphConverter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Convert from old format (array of edges with from/to/weight) to new format.
     *
     * @param inputFilePath  path to input file (old format - array of edges)
     * @param outputFilePath path to output file (new format)
     * @param source         source vertex for pathfinding
     * @throws IOException if file operations fail
     */
    public static void convertOldFormatToNew(String inputFilePath, String outputFilePath, int source) throws IOException {
        // Read the old format (array of EdgeData)
        Type listType = new TypeToken<List<EdgeData>>() {}.getType();
        List<EdgeData> edgesOld;
        
        try (FileReader reader = new FileReader(inputFilePath)) {
            edgesOld = gson.fromJson(reader, listType);
        }

        if (edgesOld == null || edgesOld.isEmpty()) {
            throw new IllegalArgumentException("Input file is empty or invalid");
        }

        // Determine number of vertices
        int maxVertex = 0;
        for (EdgeData edge : edgesOld) {
            maxVertex = Math.max(maxVertex, Math.max(edge.getU(), edge.getV()));
        }
        int n = maxVertex + 1; // vertices are 0-indexed, so add 1

        // Create new format graph
        GraphData graphNew = new GraphData(
            true,  // directed
            n,     // number of vertices
            edgesOld,
            source,
            "edge" // weight_model
        );

        // Write to output file
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            gson.toJson(graphNew, writer);
        }
    }

    /**
     * Convert from old GraphData format to new format.
     *
     * @param graphData old format graph data
     * @param source    source vertex for pathfinding
     * @return new format graph data
     */
    public static GraphData convertGraphDataToNew(GraphData graphData, int source) {
        return new GraphData(
            true,
            graphData.getN(),
            graphData.getEdges(),
            source,
            "edge"
        );
    }

    /**
     * Load graph in new format.
     *
     * @param filePath path to JSON file
     * @return GraphData object
     * @throws IOException if file cannot be read
     */
    public static GraphData loadGraphNew(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GraphData.class);
        }
    }

    /**
     * Save graph in new format.
     *
     * @param graphData graph data
     * @param filePath  path to save the JSON file
     * @throws IOException if file cannot be written
     */
    public static void saveGraphNew(GraphData graphData, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(graphData, writer);
        }
    }
}
