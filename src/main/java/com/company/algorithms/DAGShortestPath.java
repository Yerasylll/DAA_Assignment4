package com.company.algorithms;

import com.company.graphRepresentation.Graph;
import com.company.metrics.Metrics;

import java.util.*;

public class DAGShortestPath {
    private Graph graph;
    private Metrics metrics;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public int[] shortestPaths(int source) {
        int n = graph.getNumVertices();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.sortDFS();

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getNeighbors(u)) {
                    int v = edge.v;
                    int w = edge.w;

                    metrics.incrementCounter("relaxations");

                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }

        metrics.stopTimer();

        return dist;
    }

    public PathResult longestPath() {
        int n = graph.getNumVertices();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);

        boolean[] hasIncoming = new boolean[n];
        for (Graph.Edge edge : graph.getEdges()) {
            hasIncoming[edge.v] = true;
        }

        for (int i = 0; i < n; i++) {
            if (!hasIncoming[i]) {
                dist[i] = 0;
            }
        }

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.sortDFS();

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] != Integer.MIN_VALUE) {
                for (Graph.Edge edge : graph.getNeighbors(u)) {
                    int v = edge.v;
                    int w = edge.w;

                    metrics.incrementCounter("relaxations");

                    if (dist[u] + w > dist[v]) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();

        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endVertex = i;
            }
        }

        List<Integer> path = reconstructPath(parent, endVertex);

        return new PathResult(path, maxDist);
    }

    public PathResult shortestPath(int source, int target) {
        int n = graph.getNumVertices();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.sortDFS();

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getNeighbors(u)) {
                    int v = edge.v;
                    int w = edge.w;

                    metrics.incrementCounter("relaxations");

                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();

        List<Integer> path = reconstructPath(parent, target);

        return new PathResult(path, dist[target]);
    }

    private List<Integer> reconstructPath(int[] parent, int end) {
        List<Integer> path = new ArrayList<>();
        if (end == -1) return path;

        int current = end;
        while (current != -1) {
            path.add(0, current);
            current = parent[current];
        }

        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public static class PathResult {
        private List<Integer> path;
        private int length;

        public PathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }

        public List<Integer> getPath() {
            return path;
        }

        public int getLength() {
            return length;
        }
    }
}
