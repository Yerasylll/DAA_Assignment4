package com.company.benchmarkRunner;

import com.company.algorithms.DAGShortestPath;
import com.company.algorithms.TarjanSCC;
import com.company.algorithms.TopologicalSort;
import com.company.graphRepresentation.Graph;
import com.company.metrics.resultWriter.ResultWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BenchmarkRunner {

    public static void main(String[] args) {
        String[] datasets = {
                "data/tasks.json",
                "data/small_dag.json",
                "data/small_cyclic.json",
                "data/small_sparse.json",
                "data/medium_dag.json",
                "data/medium_cyclic.json",
                "data/medium_dense.json",
                "data/large_dag.json",
                "data/large_cyclic.json",
                "data/large_dense.json",
                "data/tasks (1).json"
        };

        ResultWriter writer = new ResultWriter();

        System.out.println("=".repeat(70));
        System.out.println("Batch executor - Processing all datasets");
        System.out.println("=".repeat(70));

        for (String datasetPath : datasets) {
            File file = new File(datasetPath);
            if (!file.exists()) {
                System.out.println("\nSkipping " + datasetPath + " (file not found)");
                continue;
            }

            System.out.println("\n" + "=".repeat(70));
            System.out.println("Processing: " + datasetPath);
            System.out.println("=".repeat(70));

            try {
                ResultWriter.Result result = analyzeDataset(datasetPath);
                writer.addResult(result);

            } catch (Exception e) {
                System.err.println("Error processing " + datasetPath + ": " + e.getMessage());
            }
        }

        // Write results to CSV
        writer.writeToCSV("results.csv");
        writer.printSummary();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("Benchmark completed.");
        System.out.println("=".repeat(70));
    }

    private static ResultWriter.Result analyzeDataset(String filename) throws IOException {
        String datasetName = new File(filename).getName().replace(".json", "");
        ResultWriter.Result result = new ResultWriter.Result(datasetName);

        // Load graph
        Graph graph = Graph.fromJson(filename);
        result.vertices = graph.getNumVertices();
        result.edges = graph.getEdges().size();

        System.out.println("Graph: " + result.vertices + " vertices, " + result.edges + " edges");

        // SCC Analysis
        System.out.println("\n--- SCC Analysis ---");
        TarjanSCC sccFinder = new TarjanSCC(graph);
        List<List<Integer>> sccs = sccFinder.findSCCs();

        result.numSCCs = sccs.size();
        result.largestSCC = sccs.stream().mapToInt(List::size).max().orElse(0);
        result.type = (result.numSCCs == result.vertices) ? "DAG" : "Cyclic";
        result.sccTime = sccFinder.getMetrics().getElapsedTime();
        result.sccDFSVisits = sccFinder.getMetrics().getCounter("DFS_visits");
        result.sccEdgesExplored = sccFinder.getMetrics().getCounter("edges_explored");

        System.out.println("SCCs: " + result.numSCCs + ", Largest: " + result.largestSCC);
        System.out.println("Type: " + result.type);
        System.out.println("Time: " + (result.sccTime / 1_000_000.0) + " ms");

        // Build condensation graph
        Graph condensation = sccFinder.buildCondensationGraph();

        // Topological Sort
        System.out.println("\n--- Topological Sort ---");
        TopologicalSort topoSort = new TopologicalSort(condensation);
        List<Integer> topoOrder = topoSort.sortKahn();

        result.topoTime = topoSort.getMetrics().getElapsedTime();
        result.topoOperations = topoSort.getMetrics().getCounter("queue_pops") +
                topoSort.getMetrics().getCounter("queue_pushes");

        System.out.println("Time: " + (result.topoTime / 1_000_000.0) + " ms");
        System.out.println("Operations: " + result.topoOperations);

        // Shortest Paths
        if (topoOrder.size() > 0) {
            System.out.println("\n--- Shortest Paths ---");
            int source = graph.getSource() != null ? graph.getSource() : 0;
            int sourceSCC = 0;
            for (int i = 0; i < sccs.size(); i++) {
                if (sccs.get(i).contains(source)) {
                    sourceSCC = i;
                    break;
                }
            }

            DAGShortestPath dagsp = new DAGShortestPath(condensation);
            dagsp.shortestPaths(sourceSCC);

            result.shortestPathTime = dagsp.getMetrics().getElapsedTime();
            result.shortestPathRelaxations = dagsp.getMetrics().getCounter("relaxations");

            System.out.println("Time: " + (result.shortestPathTime / 1_000_000.0) + " ms");
            System.out.println("Relaxations: " + result.shortestPathRelaxations);

            // Longest Path
            System.out.println("\n--- Critical Path ---");
            DAGShortestPath dagspLongest = new DAGShortestPath(condensation);
            DAGShortestPath.PathResult longestPath = dagspLongest.longestPath();

            result.criticalPathLength = longestPath.getLength();
            result.longestPathTime = dagspLongest.getMetrics().getElapsedTime();
            result.longestPathRelaxations = dagspLongest.getMetrics().getCounter("relaxations");

            System.out.println("Length: " + result.criticalPathLength);
            System.out.println("Time: " + (result.longestPathTime / 1_000_000.0) + " ms");
        }

        return result;
    }
}

