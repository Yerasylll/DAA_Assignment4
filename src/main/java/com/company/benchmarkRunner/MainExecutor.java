package com.company.benchmarkRunner;

import com.company.algorithms.DAGShortestPath;
import com.company.algorithms.TarjanSCC;
import com.company.algorithms.TopologicalSort;
import com.company.graphRepresentation.Graph;

import java.io.IOException;
import java.util.List;

public class MainExecutor {

    public static void main(String[] args) {
        String filename;

        if (args.length < 1) {
            // Default to tasks.json if no argument provided
            filename = "data/large_dag.json";
            System.out.println("No file specified, using default: " + filename);
        } else {
            filename = args[0];
        }

        try {
            Graph graph = Graph.fromJson(filename);
            System.out.println("Graph loaded: " + graph.getNumVertices() + " vertices, " + graph.getEdges().size() + " edges\n");

            // Find SCCs
            System.out.println("=== Strongly Connected Components ===");
            TarjanSCC sccFinder = new TarjanSCC(graph);
            List<List<Integer>> sccs = sccFinder.findSCCs();
            System.out.println("Found " + sccs.size() + " SCCs:");
            for (int i = 0; i < sccs.size(); i++) {
                System.out.println("  SCC " + i + ": " + sccs.get(i));
            }
            sccFinder.getMetrics().printMetrics();

            // Build condensation graph
            System.out.println("\n=== Building Condensation Graph ===");
            Graph condensation = sccFinder.buildCondensationGraph();
            System.out.println("Condensation: " + condensation.getNumVertices() + " nodes, " + condensation.getEdges().size() + " edges\n");

            // Topological Sort
            System.out.println("=== STEP 2: Topological Sort ===");
            TopologicalSort topoSort = new TopologicalSort(condensation);
            List<Integer> topoOrder = topoSort.sortKahn();
            System.out.println("Order: " + topoOrder);
            topoSort.getMetrics().printMetrics();

            System.out.println("\nTask execution order:");
            for (int sccIndex : topoOrder) {
                System.out.println("  " + sccs.get(sccIndex));
            }

            // Shortest Paths
            System.out.println("\n=== Shortest Paths ===");
            int source = graph.getSource() != null ? graph.getSource() : 0;
            int sourceSCC = -1;
            for (int i = 0; i < sccs.size(); i++) {
                if (sccs.get(i).contains(source)) {
                    sourceSCC = i;
                    break;
                }
            }

            if (sourceSCC != -1) {
                DAGShortestPath dagsp = new DAGShortestPath(condensation);
                int[] distances = dagsp.shortestPaths(sourceSCC);
                System.out.println("Shortest paths from source " + source + " (SCC " + sourceSCC + "):");
                for (int i = 0; i < distances.length; i++) {
                    if (distances[i] == Integer.MAX_VALUE) {
                        System.out.println("  To SCC " + i + ": unreachable");
                    } else {
                        System.out.println("  To SCC " + i + ": " + distances[i]);
                    }
                }
                dagsp.getMetrics().printMetrics();
            }

            // Critical Path
            System.out.println("\n=== Critical Path (Longest Path) ===");
            DAGShortestPath dagspLongest = new DAGShortestPath(condensation);
            DAGShortestPath.PathResult longestPath = dagspLongest.longestPath();
            System.out.println("Path: " + longestPath.getPath());
            System.out.println("Length: " + longestPath.getLength());
            dagspLongest.getMetrics().printMetrics();

            System.out.println("\n=== Analysis Complete ===");

        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
            System.err.println("Make sure the file exists and the path is correct.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}