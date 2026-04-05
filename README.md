# CSC483 – Algorithms Assignment
**Course:** CSC 483.1 – Algorithms Analysis and Design  
**Session:** 2025/2026 | **Submission:** April 5, 2026

---

## Project Structure
```
src/
  com/csc483/
    search/
      Product.java          – Product entity class
      SearchAlgorithms.java – Sequential & binary search
      HybridSearch.java     – HashMap + sorted-list hybrid
      TechMartMain.java     – Q1 benchmark driver
    sorting/
      SortResult.java         – Benchmark result container
      SortingAlgorithms.java  – Insertion, Merge, Quick, Heap sort
      SortingBenchmark.java   – Q2 empirical analysis driver
    test/
      SearchTest.java    – JUnit 5 tests for Q1
      SortingTest.java   – JUnit 5 tests for Q2
datasets/
  sample_products.csv    – 20-product sample dataset
```

---

## Compilation (without Maven/Gradle)

```bash
# From project root – compile all sources
javac -cp "lib/junit-5.jar:." -d out \
  src/com/csc483/search/*.java \
  src/com/csc483/sorting/*.java \
  src/com/csc483/test/*.java
```

---

## Running

### Question 1 – TechMart Search Benchmark
```bash
java -cp out com.csc483.search.TechMartMain
```

### Question 2 – Sorting Algorithms Benchmark
```bash
java -cp out com.csc483.sorting.SortingBenchmark
```

### JUnit Tests (with JUnit 5 on classpath)
```bash
java -cp "out:lib/junit-platform-console-standalone.jar" \
  org.junit.platform.console.ConsoleLauncher \
  --scan-classpath
```

---

## Dependencies
- Java 11 or higher
- JUnit 5 (`junit-platform-console-standalone-5.x.jar`) for tests only

---

## Sample Usage
```
================================================================
  TECHMART SEARCH PERFORMANCE ANALYSIS (n = 100,000 products)
================================================================
SEQUENTIAL SEARCH:
  Best Case  (target at index 0):    0.0012 ms   comparisons: 1
  Average Case (target at midpoint): 28.4300 ms  comparisons: 50,000
  Worst Case (target not found):     56.8100 ms  comparisons: 100,000

BINARY SEARCH:
  Best Case  (target at midpoint):   0.0008 ms   comparisons: 1
  Average Case (random target):      0.0120 ms   comparisons: 17
  Worst Case (target not found):     0.0140 ms   comparisons: 17
```

## Known Limitations
- Quick Sort uses a last-element pivot, which causes O(n²) on already-sorted data
- HybridSearch ArrayList insert is O(n) due to shifting; a balanced BST would be O(log n)
- Timing results vary with JVM warm-up and CPU load; averages over 5 runs are used
