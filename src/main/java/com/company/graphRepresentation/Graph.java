package com.company.graphRepresentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private int numVertices;
    private List<Edge> edges;
    private List<List<Edge>> adjList;
    private Integer source;
    private String weightModel;

    public static class Edge {
        public int u;
        public int v;
        public int w;

        public Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.edges = new ArrayList<>();
        this.adjList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public static Graph fromJson(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();

        String json = content.toString();
        int n = extractInt(json, "\"n\"");
        Graph graph = new Graph(n);

        int edgesStart = json.indexOf("\"edges\"");
        int arrayStart = json.indexOf('[', edgesStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);
        String edgesJson = json.substring(arrayStart + 1, arrayEnd);

        int start = 0;
        while (start < edgesJson.length()) {
            int objStart = edgesJson.indexOf('{', start);
            if (objStart == -1) break;
            int objEnd = edgesJson.indexOf('}', objStart);
            String edgeJson = edgesJson.substring(objStart, objEnd + 1);

            int u = extractInt(edgeJson, "\"u\"");
            int v = extractInt(edgeJson, "\"v\"");
            int w = extractInt(edgeJson, "\"w\"");
            graph.addEdge(u, v, w);

            start = objEnd + 1;
        }

        try {
            graph.source = extractInt(json, "\"source\"");
        } catch (Exception e) {
            graph.source = 0;
        }

        try {
            graph.weightModel = extractString(json, "\"weight_model\"");
        } catch (Exception e) {
            graph.weightModel = "edge";
        }

        return graph;
    }

    private static int extractInt(String json, String key) {
        int keyIndex = json.indexOf(key);
        int colonIndex = json.indexOf(':', keyIndex);
        int commaIndex = json.indexOf(',', colonIndex);
        int braceIndex = json.indexOf('}', colonIndex);
        int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;
        String value = json.substring(colonIndex + 1, endIndex).trim();
        return Integer.parseInt(value);
    }

    private static String extractString(String json, String key) {
        int keyIndex = json.indexOf(key);
        int colonIndex = json.indexOf(':', keyIndex);
        int quoteStart = json.indexOf('"', colonIndex);
        int quoteEnd = json.indexOf('"', quoteStart + 1);
        return json.substring(quoteStart + 1, quoteEnd);
    }

    private static int findMatchingBracket(String json, int start) {
        int count = 1;
        for (int i = start + 1; i < json.length(); i++) {
            if (json.charAt(i) == '[') count++;
            if (json.charAt(i) == ']') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }

    public void addEdge(int u, int v, int w) {
        Edge edge = new Edge(u, v, w);
        edges.add(edge);
        if (u < adjList.size()) {
            adjList.get(u).add(edge);
        }
    }

    public int getNumVertices() {
        return numVertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getNeighbors(int u) {
        return adjList.get(u);
    }

    public Integer getSource() {
        return source;
    }

    public String getWeightModel() {
        return weightModel;
    }
}
