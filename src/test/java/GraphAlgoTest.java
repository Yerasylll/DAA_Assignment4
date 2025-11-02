import com.company.algorithms.DAGShortestPath;
import com.company.algorithms.TarjanSCC;
import com.company.algorithms.TopologicalSort;
import com.company.graphRepresentation.Graph;
import com.company.metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class GraphAlgoTest {

    private Graph testGraph;

    @BeforeEach
    public void setup() {
        // Reset for each test
        testGraph = null;
    }

    // ========== SCC Tests ==========

    @Test
    public void testSCC_SimpleCycle() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();

        assertEquals(1, sccs.size(), "Should have 1 SCC");
        assertEquals(3, sccs.get(0).size(), "SCC should contain 3 vertices");
    }

    @Test
    public void testSCC_PureDAG() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();

        assertEquals(4, sccs.size(), "DAG should have 4 SCCs (one per vertex)");
    }

    @Test
    public void testSCC_MultipleCycles() {
        Graph graph = new Graph(6);
        // Cycle 1: 0 <-> 1
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        // Cycle 2: 2 <-> 3
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        // Connection
        graph.addEdge(1, 2, 1);
        // Isolated: 4 -> 5
        graph.addEdge(4, 5, 1);

        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();

        assertTrue(sccs.size() >= 3, "Should have at least 3 SCCs");
    }

    @Test
    public void testSCC_SelfLoop() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 0, 1);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();

        assertEquals(3, sccs.size(), "Self-loop creates its own SCC");
    }

    @Test
    public void testCondensationGraph() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 1, 1); // Cycle
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        TarjanSCC scc = new TarjanSCC(graph);
        Graph condensation = scc.buildCondensationGraph();

        assertTrue(condensation.getNumVertices() < graph.getNumVertices(),
                "Condensation should have fewer vertices");
        assertNotNull(condensation);
    }

    // ========== Topological Sort Tests ==========

    @Test
    public void testTopoSort_LinearDAG() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.sortKahn();

        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testTopoSort_DiamondDAG() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.sortKahn();

        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testTopoSort_EmptyGraph() {
        Graph graph = new Graph(3);

        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.sortKahn();

        assertEquals(3, order.size(), "All vertices should be in result");
    }

    @Test
    public void testTopoSort_DFSvsKahn() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        TopologicalSort topo1 = new TopologicalSort(graph);
        TopologicalSort topo2 = new TopologicalSort(graph);

        List<Integer> orderKahn = topo1.sortKahn();
        List<Integer> orderDFS = topo2.sortDFS();

        assertEquals(5, orderKahn.size());
        assertEquals(5, orderDFS.size());
        // Both should respect dependencies
        assertTrue(orderKahn.indexOf(0) < orderKahn.indexOf(1));
        assertTrue(orderDFS.indexOf(0) < orderDFS.indexOf(1));
    }

    // ========== Shortest Path Tests ==========

    @Test
    public void testShortestPath_Simple() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 4);

        DAGShortestPath dagsp = new DAGShortestPath(graph);
        int[] distances = dagsp.shortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(7, distances[3]);
    }

    @Test
    public void testShortestPath_Unreachable() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);

        DAGShortestPath dagsp = new DAGShortestPath(graph);
        int[] distances = dagsp.shortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(1, distances[1]);
        assertEquals(Integer.MAX_VALUE, distances[2]);
        assertEquals(Integer.MAX_VALUE, distances[3]);
    }

    @Test
    public void testLongestPath_Simple() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 2);

        DAGShortestPath dagsp = new DAGShortestPath(graph);
        DAGShortestPath.PathResult result = dagsp.longestPath();

        assertTrue(result.getLength() >= 9);
        assertNotNull(result.getPath());
        assertTrue(result.getPath().size() > 0);
    }

    @Test
    public void testLongestPath_Linear() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 5);

        DAGShortestPath dagsp = new DAGShortestPath(graph);
        DAGShortestPath.PathResult result = dagsp.longestPath();

        assertEquals(10, result.getLength());
        assertEquals(4, result.getPath().size());
    }

    @Test
    public void testPathReconstruction() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);

        DAGShortestPath dagsp = new DAGShortestPath(graph);
        DAGShortestPath.PathResult result = dagsp.shortestPath(0, 3);

        assertEquals(6, result.getLength());
        assertTrue(result.getPath().contains(0));
        assertTrue(result.getPath().contains(3));
        assertEquals(0, (int) result.getPath().get(0));
        assertEquals(3, (int) result.getPath().get(result.getPath().size() - 1));
    }

    // ========== Metrics Tests ==========

    @Test
    public void testMetrics_CounterIncrement() {
        Metrics metrics = new Metrics();

        metrics.incrementCounter("test");
        metrics.incrementCounter("test");
        metrics.incrementCounter("test");

        assertEquals(3, metrics.getCounter("test"));
    }

    @Test
    public void testMetrics_Timer() {
        Metrics metrics = new Metrics();

        metrics.startTimer();
        // Do some work
        for (int i = 0; i < 1000; i++) {
            int x = i * i;
        }
        metrics.stopTimer();

        assertTrue(metrics.getElapsedTime() > 0);
    }

    @Test
    public void testMetrics_Reset() {
        Metrics metrics = new Metrics();

        metrics.incrementCounter("test");
        metrics.startTimer();
        metrics.stopTimer();
        metrics.reset();

        assertEquals(0, metrics.getCounter("test"));
        assertEquals(0, metrics.getElapsedTime());
    }

    // ========== Integration Tests ==========

    @Test
    public void testFullPipeline_SimpleGraph() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 1, 1); // Cycle
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        // SCC
        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();
        assertNotNull(sccs);

        // Condensation
        Graph condensation = scc.buildCondensationGraph();
        assertNotNull(condensation);

        // Topological Sort
        TopologicalSort topo = new TopologicalSort(condensation);
        List<Integer> order = topo.sortKahn();
        assertEquals(condensation.getNumVertices(), order.size());

        // Shortest Paths
        DAGShortestPath dagsp = new DAGShortestPath(condensation);
        int[] distances = dagsp.shortestPaths(0);
        assertNotNull(distances);
    }

    @Test
    public void testPerformance_LargeGraph() {
        Graph graph = new Graph(100);
        for (int i = 0; i < 99; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        TarjanSCC scc = new TarjanSCC(graph);
        List<List<Integer>> sccs = scc.findSCCs();

        assertEquals(100, sccs.size());
        assertTrue(scc.getMetrics().getElapsedTime() < 10_000_000); // < 10ms
    }
}