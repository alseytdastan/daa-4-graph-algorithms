package model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an edge in the graph with source, target, and weight.
 * Supports JSON format with "u", "v", "w" fields.
 */
public class EdgeData {
    @SerializedName("u")
    private int u;
    
    @SerializedName("v")
    private int v;
    
    @SerializedName("w")
    private double w;

    public EdgeData() {
    }

    public EdgeData(int u, int v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    // Legacy getters/setters for backward compatibility (from/to/weight)
    public int getFrom() {
        return u;
    }

    public void setFrom(int from) {
        this.u = from;
    }

    public int getTo() {
        return v;
    }

    public void setTo(int to) {
        this.v = to;
    }

    public double getWeight() {
        return w;
    }

    public void setWeight(double weight) {
        this.w = weight;
    }

    @Override
    public String toString() {
        return "EdgeData{" +
                "u=" + u +
                ", v=" + v +
                ", w=" + w +
                '}';
    }
}
