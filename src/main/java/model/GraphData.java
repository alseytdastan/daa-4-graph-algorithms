package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the complete graph structure loaded from JSON.
 * Supports the assignment format with directed, n, edges, source, weight_model fields.
 */
public class GraphData {
    @SerializedName("directed")
    private boolean directed;
    
    @SerializedName("n")
    private int n;
    
    @SerializedName("edges")
    private List<EdgeData> edges;
    
    @SerializedName("source")
    private int source;
    
    @SerializedName("weight_model")
    private String weightModel;

    public GraphData() {
    }

    public GraphData(boolean directed, int n, List<EdgeData> edges, int source, String weightModel) {
        this.directed = directed;
        this.n = n;
        this.edges = edges;
        this.source = source;
        this.weightModel = weightModel;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public List<EdgeData> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeData> edges) {
        this.edges = edges;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getWeightModel() {
        return weightModel;
    }

    public void setWeightModel(String weightModel) {
        this.weightModel = weightModel;
    }

    // Legacy getters/setters for backward compatibility (vertices)
    public int getVertices() {
        return n;
    }

    public void setVertices(int vertices) {
        this.n = vertices;
    }
}
