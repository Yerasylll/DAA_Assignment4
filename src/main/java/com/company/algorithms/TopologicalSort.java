package com.company.algorithms;


import com.company.graphRepresentation.Graph;
import com.company.metrics.Metrics;

import java.util.*;

public class TopologicalSort {
    private Graph graph;
    private Metrics metrics;

    public TopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public List<Integer> sortKahn() {
        int n = graph.getNumVertices();
        int[] inDegree = new int[n];

        for (Graph.Edge edge : graph.getEdges()) {
            inDegree[edge.v]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();

        metrics.startTimer();

        while (!queue.isEmpty()) {
            metrics.incrementCounter("queue_pops");
            int u = queue.poll();
            result.add(u);

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.v;
                inDegree[v]--;

                if (inDegree[v] == 0) {
                    metrics.incrementCounter("queue_pushes");
                    queue.offer(v);
                }
            }
        }

        metrics.stopTimer();

        if (result.size() != n) {
            return new ArrayList<>();
        }

        return result;
    }

    public List<Integer> sortDFS() {
        int n = graph.getNumVertices();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        metrics.startTimer();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsUtil(i, visited, stack);
            }
        }

        metrics.stopTimer();

        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

    private void dfsUtil(int u, boolean[] visited, Stack<Integer> stack) {
        metrics.incrementCounter("DFS_visits");
        visited[u] = true;

        for (Graph.Edge edge : graph.getNeighbors(u)) {
            if (!visited[edge.v]) {
                dfsUtil(edge.v, visited, stack);
            }
        }

        stack.push(u);
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
