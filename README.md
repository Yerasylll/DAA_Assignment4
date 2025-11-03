# Smart City/Campus Scheduler - Assignment 4

## Project Overview

This project implements graph algorithms for scheduling city-service tasks with dependencies, including:
- **Tarjan's SCC Algorithm** - Detecting strongly connected components
- **Topological Sorting** - Ordering tasks with dependencies
- **DAG Shortest/Longest Paths** - Finding optimal paths and critical paths

## Project Structure

```
DAA_Assignment4/
├── src/
│   ├── main/java/com/company/
│   │   ├── algorithms/
│   │   │   ├── DAGShortestPath.java
│   │   │   ├── TarjanSCC.java
│   │   │   └── TopologicalSort.java
│   │   ├── benchmarkRunner/
│   │   │   ├── BenchmarkRunner.java
│   │   │   └── MainExecutor.java
│   │   ├── graphRepresentation/
│   │   │   └── Graph.java
│   │   └── metrics/
│   │       ├── resultWriter/
│   │       │   └── ResultWriter.java
│   │       └── Metrics.java
│   └── test/java/
│       └── GraphAlgoTest.java
├── data/
│   ├── tasks (1).json
│   ├── small_dag.json
│   ├── small_cyclic.json
│   ├── small_sparse.json
│   ├── medium_dag.json
│   ├── medium_cyclic.json
│   ├── medium_dense.json
│   ├── large_dag.json
│   ├── large_cyclic.json
│   └── large_dense.json
├── pom.xml
└── README.md
```

## Requirements

- Java 11 or higher
- Maven (optional, for dependency management)

## Compilation

### Using Maven:
```bash
mvn clean compile
```

### Using javac:
```bash
# From project root
javac -d target/classes -sourcepath src/main/java src/main/java/com/company/**/*.java
```

## Running the Application

### Run MainExecutor (Single Dataset):
```bash
# With Maven
mvn exec:java -Dexec.mainClass="com.company.benchmarkRunner.MainExecutor" -Dexec.args="data/tasks (1).json"

# With java
java -cp target/classes com.company.benchmarkRunner.MainExecutor data/tasks (1).json
```

### Run BenchmarkRunner (All Datasets):
```bash
# With Maven
mvn exec:java -Dexec.mainClass="com.company.benchmarkRunner.BenchmarkRunner"

# With java
java -cp target/classes com.company.benchmarkRunner.BenchmarkRunner
```

This will:
- Process all 10 datasets
- Generate `results.csv` with performance metrics
- Print summary statistics

## Input Format

JSON files in `data/` directory follow this format:

```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2}
  ],
  "source": 4,
  "weight_model": "edge"
}
```

- `n`: Number of vertices
- `edges`: Array of directed edges (u -> v with weight w)
- `source`: Starting vertex for shortest path calculations
- `weight_model`: "edge" (using edge weights)

## Datasets

### Small (6-10 vertices)
- **small_dag.json** - Pure DAG, 8 vertices, 10 edges
- **small_cyclic.json** - Contains cycles, 7 vertices, 12 edges
- **small_sparse.json** - Sparse connectivity, 10 vertices, 9 edges

### Medium (10-20 vertices)
- **medium_dag.json** - DAG structure, 15 vertices, 25 edges
- **medium_cyclic.json** - Multiple SCCs, 12 vertices, 20 edges
- **medium_dense.json** - Dense graph, 18 vertices, 40 edges

### Large (20-50 vertices)
- **large_dag.json** - Large DAG, 30 vertices, 60 edges
- **large_cyclic.json** - Complex cycles, 25 vertices, 50 edges
- **large_dense.json** - Very dense, 40 vertices, 120 edges

### Sample
- **tasks (1).json** - From assignment specification, 8 vertices, 7 edges

## Algorithms Implemented

### 1. Tarjan's SCC Algorithm
**File:** `algorithms/TarjanSCC.java`

- Detects strongly connected components in O(V + E) time
- Uses DFS with discovery times and low-link values
- Builds condensation graph (DAG of SCCs)

**Metrics tracked:**
- DFS visits
- Edges explored
- Execution time

### 2. Topological Sorting
**File:** `algorithms/TopologicalSort.java`

Two implementations:
- **Kahn's Algorithm** - BFS-based, O(V + E)
- **DFS-based** - Recursive, O(V + E)

**Metrics tracked:**
- Queue operations (Kahn's)
- DFS visits
- Execution time

### 3. DAG Shortest/Longest Paths
**File:** `algorithms/DAGShortestPath.java`

- **Shortest paths** - Single-source, using DP over topological order
- **Longest path** - Critical path for scheduling, O(V + E)
- **Path reconstruction** - Returns actual path, not just distance

**Metrics tracked:**
- Edge relaxations
- Execution time

## Output

### Console Output Example:
```
=== STEP 1: Strongly Connected Components ===
Found 6 SCCs:
  SCC 0: [3, 2, 1]
  SCC 1: [0]
  ...

=== Metrics ===
Time: 0.142 ms
DFS_visits: 8
edges_explored: 7
===============

=== STEP 2: Topological Sort ===
Order: [1, 5, 0, 4, 3, 2]
...

=== STEP 3: Shortest Paths ===
Shortest paths from source 4 (SCC 5):
  To SCC 0: unreachable
  To SCC 2: 8
  ...

=== Critical Path (Longest Path) ===
Path: [5, 4, 3, 2]
Length: 8
```

### CSV Output (results.csv):
```csv
Dataset,Vertices,Edges,Type,NumSCCs,LargestSCC,SCC_Time_ms,SCC_DFS_Visits,...
tasks,8,7,Cyclic,6,3,0.142,8,7,...
small_dag,8,10,DAG,8,1,0.089,8,10,...
```

**Columns:**
- Dataset name
- Graph size (vertices, edges)
- Type (DAG or Cyclic)
- Number of SCCs
- Largest SCC size
- SCC performance (time, DFS visits, edges explored)
- Topological sort performance (time, operations)
- Shortest path performance (time, relaxations)
- Critical path length
- Longest path performance (time, relaxations)

## Testing

Run tests:
```bash
mvn test
```

Test file: `src/test/java/GraphAlgoTest.java`

**Test Coverage:**
- SCC detection (cycles, DAGs, multiple components)
- Topological sorting (both algorithms)
- Shortest/longest paths
- Path reconstruction
- Performance validation

## Performance Characteristics

All algorithms achieve **O(V + E)** time complexity:

| Algorithm | Time | Space | Verified |
|-----------|------|-------|------|
| Tarjan SCC | O(V + E) | O(V) | yes     |
| Topological Sort | O(V + E) | O(V) | yes     |
| DAG Shortest Path | O(V + E) | O(V) | yes     |
| DAG Longest Path | O(V + E) | O(V) | yes  |

## Key Design Decisions

### 1. Package Organization
- `algorithms/` - Core graph algorithms
- `graphRepresentation/` - Graph data structure
- `metrics/` - Performance tracking
- `benchmarkRunner/` - Execution and analysis

### 2. Metrics Class (No Interface)
- Directly implemented as concrete class
- Tracks operation counts and timing
- Used by all algorithms for consistent measurement

### 3. Weight Model
- Uses **edge weights** (not node durations)
- More intuitive for dependency graphs
- Standard approach in literature

### 4. JSON Parsing
- Simple custom parser (no external dependencies)
- Handles the specific format required
- Lightweight and efficient

## Results Analysis

### Sample Results (from results.csv):

**Graph Size Effect:**
- Small (8 vertices): ~0.1 ms
- Medium (15 vertices): ~0.15 ms
- Large (30 vertices): ~0.25 ms
- **Confirms linear scaling O(V + E)**

**Type Effect:**
- DAG: More SCCs (one per vertex)
- Cyclic: Fewer, larger SCCs
- Slight performance difference due to SCC structure

**Operation Counts:**
- SCC: DFS visits = vertices 
- SCC: Edges explored = edges 
- Topological: Operations ≈ vertices 
- Shortest path: Relaxations = edges 

## Use Cases

### Project Management
```bash
java -cp target/classes com.company.benchmarkRunner.MainExecutor data/project_tasks.json
```
Find critical path to determine minimum project duration.

### Dependency Resolution
```bash
java -cp target/classes com.company.benchmarkRunner.MainExecutor data/dependencies.json
```
Detect circular dependencies in build systems.

### Task Scheduling
Use topological order for valid task execution sequence.

## Troubleshooting

### Issue: "No such file or directory"
**Solution:** Create `data/` directory in project root

### Issue: "ClassNotFoundException"
**Solution:** Compile all files first, check classpath

### Issue: "Package does not exist"
**Solution:** Verify package structure matches file organization

## Dependencies

**Runtime:** None (pure Java implementation)

**Testing:** JUnit 5 (optional)
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```


## References

- Tarjan, R. (1972). "Depth-first search and linear graph algorithms"
- Cormen, T. H., et al. (2009). "Introduction to Algorithms" (3rd ed.)