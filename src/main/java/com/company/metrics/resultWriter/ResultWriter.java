package com.company.metrics.resultWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ResultWriter {

    public static class Result {
        public String dataset;
        public int vertices;
        public int edges;
        public String type;
        public int numSCCs;
        public int largestSCC;
        public long sccTime;
        public long sccDFSVisits;
        public long sccEdgesExplored;
        public long topoTime;
        public long topoOperations;
        public long shortestPathTime;
        public long shortestPathRelaxations;
        public int criticalPathLength;
        public long longestPathTime;
        public long longestPathRelaxations;

        public Result(String dataset) {
            this.dataset = dataset;
        }
    }

    private List<Result> results;

    public ResultWriter() {
        this.results = new ArrayList<>();
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public void writeToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Dataset,Vertices,Edges,Type,NumSCCs,LargestSCC," +
                    "SCC_Time_ms,SCC_DFS_Visits,SCC_Edges_Explored," +
                    "Topo_Time_ms,Topo_Operations," +
                    "ShortestPath_Time_ms,ShortestPath_Relaxations," +
                    "CriticalPath_Length,LongestPath_Time_ms,LongestPath_Relaxations");

            // Write data
            for (Result r : results) {
                writer.printf("%s,%d,%d,%s,%d,%d,%.3f,%d,%d,%.3f,%d,%.3f,%d,%d,%.3f,%d%n",
                        r.dataset,
                        r.vertices,
                        r.edges,
                        r.type,
                        r.numSCCs,
                        r.largestSCC,
                        r.sccTime / 1_000_000.0,
                        r.sccDFSVisits,
                        r.sccEdgesExplored,
                        r.topoTime / 1_000_000.0,
                        r.topoOperations,
                        r.shortestPathTime / 1_000_000.0,
                        r.shortestPathRelaxations,
                        r.criticalPathLength,
                        r.longestPathTime / 1_000_000.0,
                        r.longestPathRelaxations
                );
            }

            System.out.println("\nResults written to: " + filename);

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    public void printSummary() {
        System.out.println("\n=== RESULTS SUMMARY ===");
        System.out.println(String.format("%-20s %8s %8s %8s %10s",
                "Dataset", "Vertices", "Edges", "SCCs", "SCC_Time_ms"));
        System.out.println("-".repeat(60));

        for (Result r : results) {
            System.out.println(String.format("%-20s %8d %8d %8d %10.3f",
                    r.dataset,
                    r.vertices,
                    r.edges,
                    r.numSCCs,
                    r.sccTime / 1_000_000.0
            ));
        }
        System.out.println("=".repeat(60));
    }
}