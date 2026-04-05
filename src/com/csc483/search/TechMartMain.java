package com.csc483.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Question 1 – TechMart Search Performance Analysis
 *
 * <p>Generates 100,000 random products, sorts them, then benchmarks
 * sequential search, binary search, and the hybrid approach across
 * best, average, and worst cases. Prints a formatted comparison table.
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public class TechMartMain {

    // ── Constants ─────────────────────────────────────────────────────────
    private static final int    DATASET_SIZE    = 100_000;
    private static final int    MAX_PRODUCT_ID  = 200_000;
    private static final int    TIMING_RUNS     = 5;          // average over 5 runs
    private static final long   SEED            = 42L;

    private static final String[] CATEGORIES = {
        "Electronics", "Phones", "Laptops", "Tablets", "TVs",
        "Cameras", "Audio", "Wearables", "Gaming", "Accessories"
    };
    private static final String[] NAME_PREFIXES = {
        "ProMax", "UltraX", "SmartGo", "TechPro", "NovaSeries",
        "ApexOne", "SwiftDrive", "ClearVision", "PowerCore", "ZenBook"
    };

    // ── Main ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("================================================================");
        System.out.printf( "  TECHMART SEARCH PERFORMANCE ANALYSIS (n = %,d products)%n",
                           DATASET_SIZE);
        System.out.println("================================================================");

        // ── 1. Generate dataset ──────────────────────────────────────────
        System.out.println("\n[INFO] Generating dataset...");
        Product[] products = generateProducts(DATASET_SIZE);

        // ── 2. Sort by productId (required for binary search) ────────────
        Arrays.sort(products);   // uses Product.compareTo (by productId)
        System.out.println("[INFO] Array sorted by productId. Binary search ready.\n");

        // ── 3. Select test targets ───────────────────────────────────────
        int bestId   = products[0].getProductId();                  // index 0  → 1 comparison
        int avgId    = products[DATASET_SIZE / 2].getProductId();   // midpoint  → ≈ log n
        int worstId  = MAX_PRODUCT_ID + 1;                          // never exists → full scan

        // ── 4. Sequential search benchmarks ─────────────────────────────
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("  SEQUENTIAL SEARCH  [O(n) worst/average,  O(1) best]");
        System.out.println("───────────────────────────────────────────────────────────────");
        double seqBest  = timeSearch(SearchType.SEQUENTIAL, products, bestId);
        double seqAvg   = timeSearch(SearchType.SEQUENTIAL, products, avgId);
        double seqWorst = timeSearch(SearchType.SEQUENTIAL, products, worstId);

        long[] seqBestCnt  = SearchAlgorithms.sequentialSearchCount(products, bestId);
        long[] seqAvgCnt   = SearchAlgorithms.sequentialSearchCount(products, avgId);
        long[] seqWorstCnt = SearchAlgorithms.sequentialSearchCount(products, worstId);

        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Best Case  (target at index 0):",   seqBest,  seqBestCnt[1]);
        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Average Case (target at midpoint):", seqAvg,  seqAvgCnt[1]);
        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Worst Case (target not found):",    seqWorst, seqWorstCnt[1]);

        // ── 5. Binary search benchmarks ──────────────────────────────────
        System.out.println("\n───────────────────────────────────────────────────────────────");
        System.out.println("  BINARY SEARCH  [O(log n) worst/average,  O(1) best]");
        System.out.println("───────────────────────────────────────────────────────────────");
        double binBest  = timeSearch(SearchType.BINARY, products, bestId);
        double binAvg   = timeSearch(SearchType.BINARY, products, avgId);
        double binWorst = timeSearch(SearchType.BINARY, products, worstId);

        long[] binBestCnt  = SearchAlgorithms.binarySearchCount(products, bestId);
        long[] binAvgCnt   = SearchAlgorithms.binarySearchCount(products, avgId);
        long[] binWorstCnt = SearchAlgorithms.binarySearchCount(products, worstId);

        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Best Case  (target at midpoint):",  binBest,  binBestCnt[1]);
        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Average Case (random target):",     binAvg,   binAvgCnt[1]);
        System.out.printf("  %-40s %8.4f ms   comparisons: %,d%n",
            "Worst Case (target not found):",    binWorst, binWorstCnt[1]);

        // ── 6. Performance improvement ───────────────────────────────────
        double improvement = (seqAvg > 0 && binAvg > 0) ? seqAvg / binAvg : 0;
        System.out.printf("%n  >>> PERFORMANCE IMPROVEMENT: Binary is ~%.0fx faster (avg)%n",
                          improvement);
        System.out.printf("  >>> Theoretical: n/2 / log2(n) = %.0f / %.1f = %.0fx%n%n",
                          DATASET_SIZE / 2.0,
                          Math.log(DATASET_SIZE) / Math.log(2),
                          (DATASET_SIZE / 2.0) / (Math.log(DATASET_SIZE) / Math.log(2)));

        // ── 7. Hybrid search benchmarks ──────────────────────────────────
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("  HYBRID SEARCH  [HashMap name index + sorted list by ID]");
        System.out.println("───────────────────────────────────────────────────────────────");

        HybridSearch hybrid = new HybridSearch();
        long buildStart = System.nanoTime();
        hybrid.buildIndex(products);
        double buildMs = (System.nanoTime() - buildStart) / 1_000_000.0;
        System.out.printf("  Index build time: %.3f ms%n", buildMs);

        // Warm-up
        hybrid.searchByName(products[0].getProductName());

        // Time name search (average case)
        double nameSearchMs = timeHybridName(hybrid, products, 1000);
        System.out.printf("  Average name search time (HashMap): %.4f ms   O(1)%n", nameSearchMs);

        // Time addProduct
        double insertMs = timeHybridInsert(hybrid);
        System.out.printf("  Average addProduct time:            %.4f ms   O(n) worst%n", insertMs);

        // Time searchById via hybrid
        double hybridIdMs = timeSearch(SearchType.BINARY, products, avgId);
        System.out.printf("  Binary search by ID via hybrid:     %.4f ms   O(log n)%n", hybridIdMs);

        // ── 8. Summary table ─────────────────────────────────────────────
        System.out.println("\n================================================================");
        System.out.println("  SUMMARY TABLE");
        System.out.println("================================================================");
        System.out.printf("  %-28s %-14s %-14s %-14s%n",
                          "Method", "Best (ms)", "Average (ms)", "Worst (ms)");
        System.out.println("  " + "─".repeat(72));
        System.out.printf("  %-28s %-14.4f %-14.4f %-14.4f%n",
                          "Sequential Search", seqBest, seqAvg, seqWorst);
        System.out.printf("  %-28s %-14.4f %-14.4f %-14.4f%n",
                          "Binary Search",     binBest, binAvg, binWorst);
        System.out.printf("  %-28s %-14.4f %-14s %-14s%n",
                          "Hybrid Name Search", nameSearchMs, "O(1)", "O(1)");
        System.out.printf("  %-28s %-14.4f %-14s %-14s%n",
                          "Hybrid Insert",      insertMs,     "O(log n)", "O(n)");
        System.out.println("================================================================");
    }

    // ── Search timer ──────────────────────────────────────────────────────
    private enum SearchType { SEQUENTIAL, BINARY }

    private static double timeSearch(SearchType type, Product[] products, int targetId) {
        long total = 0;
        for (int r = 0; r < TIMING_RUNS; r++) {
            long start = System.nanoTime();
            if (type == SearchType.SEQUENTIAL)
                SearchAlgorithms.sequentialSearchById(products, targetId);
            else
                SearchAlgorithms.binarySearchById(products, targetId);
            total += System.nanoTime() - start;
        }
        return (total / (double) TIMING_RUNS) / 1_000_000.0;
    }

    private static double timeHybridName(HybridSearch hybrid, Product[] products, int samples) {
        Random rnd = new Random(SEED);
        long total = 0;
        for (int i = 0; i < samples; i++) {
            String name = products[rnd.nextInt(products.length)].getProductName();
            long start = System.nanoTime();
            hybrid.searchByName(name);
            total += System.nanoTime() - start;
        }
        return (total / (double) samples) / 1_000_000.0;
    }

    private static double timeHybridInsert(HybridSearch hybrid) {
        // Insert 10 products not previously in the catalogue
        int startId = MAX_PRODUCT_ID + 100;
        long total  = 0;
        for (int i = 0; i < 10; i++) {
            Product p = new Product(startId + i, "NewProduct" + i, "Electronics",
                                    999.99, 10);
            long start = System.nanoTime();
            hybrid.addProduct(p);
            total += System.nanoTime() - start;
        }
        return (total / 10.0) / 1_000_000.0;
    }

    // ── Dataset generator ─────────────────────────────────────────────────
    /**
     * Generates n Products with unique IDs drawn from [1, MAX_PRODUCT_ID].
     */
    public static Product[] generateProducts(int n) {
        Random rnd = new Random(SEED);
        Set<Integer> usedIds = new HashSet<>(n * 2);
        Product[] arr = new Product[n];
        int i = 0;
        while (i < n) {
            int id = rnd.nextInt(MAX_PRODUCT_ID) + 1;
            if (usedIds.add(id)) {
                String name = NAME_PREFIXES[rnd.nextInt(NAME_PREFIXES.length)]
                              + "-" + id;
                String cat  = CATEGORIES[rnd.nextInt(CATEGORIES.length)];
                double price = 500.0 + rnd.nextDouble() * 499_500.0;
                int stock   = rnd.nextInt(1000);
                arr[i++] = new Product(id, name, cat, Math.round(price * 100.0) / 100.0, stock);
            }
        }
        return arr;
    }
}
