package com.company.algorithms;


import com.company.graphRepresentation.Graph;
import com.company.metrics.Metrics;

import java.util.*;

public class TarjanSCC {
    private Graph graph;
    private Metrics metrics;
    private int[] disc;
    private int[] low;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int time;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.getNumVertices();
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        time = 0;

        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);

        metrics.startTimer();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();

        return sccs;
    }

    private void dfs(int u) {
        metrics.incrementCounter("DFS_visits");

        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        for (Graph.Edge edge : graph.getNeighbors(u)) {
            int v = edge.v;
            metrics.incrementCounter("edges_explored");

            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
            } while (v != u);

            sccs.add(scc);
        }
    }

    public Graph buildCondensationGraph() {
        List<List<Integer>> sccs = findSCCs();
        int numSCCs = sccs.size();

        int[] vertexToSCC = new int[graph.getNumVertices()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToSCC[vertex] = i;
            }
        }

        Graph condensation = new Graph(numSCCs);
        Set<String> addedEdges = new HashSet<>();

        for (Graph.Edge edge : graph.getEdges()) {
            int sccU = vertexToSCC[edge.u];
            int sccV = vertexToSCC[edge.v];

            if (sccU != sccV) {
                String edgeKey = sccU + "->" + sccV;
                if (!addedEdges.contains(edgeKey)) {
                    condensation.addEdge(sccU, sccV, edge.w);
                    addedEdges.add(edgeKey);
                }
            }
        }

        return condensation;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
