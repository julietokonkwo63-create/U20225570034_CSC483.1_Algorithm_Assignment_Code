package com.csc483.sorting;

import java.util.Arrays;
import java.util.Random;

/**
 * Question 2 – Empirical sorting algorithm comparison.
 *
 * <p>Tests Insertion Sort, Merge Sort, and Quick Sort across:
 * <ul>
 *   <li>Input sizes: 100, 1,000, 10,000, 100,000</li>
 *   <li>Data types: Random, Sorted, Reversed, Nearly-Sorted, Many-Duplicates</li>
 * </ul>
 * Each measurement is averaged over 5 runs. Includes t-test between
 * Merge Sort and Quick Sort runtimes.
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public class SortingBenchmark {

    private static final int    RUNS    = 5;
    private static final long   SEED    = 12345L;
    private static final int[]  SIZES   = {100, 1_000, 10_000, 100_000};

    // ── Data type labels ─────────────────────────────────────────────────
    private static final String RANDOM        = "Random";
    private static final String SORTED        = "Sorted";
    private static final String REVERSED      = "Reversed";
    private static final String NEARLY_SORTED = "NearlySorted";
    private static final String DUPLICATES    = "Duplicates";

    // ── Algorithms ───────────────────────────────────────────────────────
    private static final String INSERTION = "Insertion";
    private static final String MERGE     = "Merge";
    private static final String QUICK     = "Quick";

    // ═══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {

        System.out.println("================================================================");
        System.out.println("      SORTING ALGORITHMS COMPARISON – CSC 483 Q2 EMPIRICAL");
        System.out.println("================================================================");
        System.out.printf("%-12s %-16s %8s  %10s  %12s  %10s%n",
                "Algorithm","Data Type","Size","Time(ms)","Comparisons","Swaps");
        System.out.println("─".repeat(76));

        // ── Main experiment loop ─────────────────────────────────────────
        for (int n : SIZES) {
            String[] labels   = {RANDOM, SORTED, REVERSED, NEARLY_SORTED, DUPLICATES};
            int[][]  datasets = {
                randomData(n), sortedData(n), reversedData(n),
                nearlySorted(n), manyDuplicates(n)
            };

            for (int d = 0; d < labels.length; d++) {
                runAndPrint(INSERTION, labels[d], n, datasets[d]);
                runAndPrint(MERGE,     labels[d], n, datasets[d]);
                runAndPrint(QUICK,     labels[d], n, datasets[d]);
                System.out.println();
            }
        }

        // ── Statistical analysis ─────────────────────────────────────────
        System.out.println("================================================================");
        System.out.println("  STATISTICAL ANALYSIS – Merge vs Quick Sort (n = 100,000 Random)");
        System.out.println("================================================================");
        int n = 100_000;
        double[] mergeTimes = collectTimes(MERGE, randomData(n), 30);
        double[] quickTimes = collectTimes(QUICK, randomData(n), 30);

        System.out.printf("  Merge Sort  – mean: %.4f ms  std: %.4f ms%n",
                mean(mergeTimes), stdDev(mergeTimes));
        System.out.printf("  Quick Sort  – mean: %.4f ms  std: %.4f ms%n",
                mean(quickTimes), stdDev(quickTimes));

        double t = tTest(mergeTimes, quickTimes);
        System.out.printf("  t-statistic: %.4f  (|t| > 2 suggests significant difference at α=0.05)%n", t);
        System.out.printf("  Conclusion : %s%n",
                Math.abs(t) > 2.0
                ? "Performance difference IS statistically significant."
                : "No statistically significant difference detected.");

        // ── Empirical vs theoretical table ───────────────────────────────
        System.out.println("\n================================================================");
        System.out.println("  EMPIRICAL vs THEORETICAL COMPLEXITY");
        System.out.println("================================================================");
        System.out.printf("  %-12s %-16s %8s  %12s  %18s%n",
                "Algorithm","Complexity","n","Empirical ops","Theoretical ops");
        System.out.println("  " + "─".repeat(72));
        for (int sz : new int[]{1_000, 10_000, 100_000}) {
            int[] data = randomData(sz);
            SortingAlgorithms.insertionSort(data);
            long insComp = SortingAlgorithms.getComparisons();
            SortingAlgorithms.mergeSort(data);
            long merComp = SortingAlgorithms.getComparisons();
            SortingAlgorithms.quickSort(data);
            long quiComp = SortingAlgorithms.getComparisons();

            long theoIns = (long)(sz * sz) / 4;  // n²/4 average
            long theoMer = (long)(sz * Math.log(sz) / Math.log(2));
            long theoQui = (long)(sz * Math.log(sz) / Math.log(2));

            System.out.printf("  %-12s %-16s %8d  %12d  %18d%n",
                    "Insertion","O(n²)",sz,insComp,theoIns);
            System.out.printf("  %-12s %-16s %8d  %12d  %18d%n",
                    "Merge","O(n log n)",sz,merComp,theoMer);
            System.out.printf("  %-12s %-16s %8d  %12d  %18d%n",
                    "Quick","O(n log n)",sz,quiComp,theoQui);
            System.out.println();
        }

        // ── Conclusions ──────────────────────────────────────────────────
        System.out.println("================================================================");
        System.out.println("  CONCLUSIONS");
        System.out.println("================================================================");
        System.out.println("  1. Quick Sort is fastest on average for random data.");
        System.out.println("  2. Insertion Sort is competitive only for n <= 1,000.");
        System.out.println("  3. Merge Sort provides consistent performance regardless of order.");
        System.out.println("  4. Quick Sort degrades to O(n²) on already-sorted data (last pivot).");
        System.out.println("  5. Insertion Sort excels on nearly-sorted data (approaches O(n)).");
        System.out.println("  6. All three O(n log n) algorithms handle duplicates similarly.");
        System.out.println("================================================================");
    }

    // ── Run one algorithm and print a result row ────────────────────────
    private static void runAndPrint(String algo, String label, int n, int[] data) {
        double totalMs = 0;
        long   lastCmp = 0, lastSwp = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            sort(algo, data.clone());
            totalMs += (System.nanoTime() - start) / 1_000_000.0;
        }
        // Record counts on one more run
        sort(algo, data.clone());
        lastCmp = SortingAlgorithms.getComparisons();
        lastSwp = SortingAlgorithms.getSwaps();

        double avgMs = totalMs / RUNS;
        String swpStr = (algo.equals(MERGE)) ? "       N/A"
                                             : String.format("%,10d", lastSwp);
        System.out.printf("%-12s %-16s %8d  %10.3f  %,12d  %s%n",
                algo, label, n, avgMs, lastCmp, swpStr);
    }

    // ── Dispatch to algorithm ────────────────────────────────────────────
    private static int[] sort(String algo, int[] data) {
        return switch (algo) {
            case INSERTION -> SortingAlgorithms.insertionSort(data);
            case MERGE     -> SortingAlgorithms.mergeSort(data);
            default        -> SortingAlgorithms.quickSort(data);
        };
    }

    // ── Collect n timing samples for statistics ──────────────────────────
    private static double[] collectTimes(String algo, int[] data, int samples) {
        double[] times = new double[samples];
        for (int i = 0; i < samples; i++) {
            long start = System.nanoTime();
            sort(algo, data.clone());
            times[i] = (System.nanoTime() - start) / 1_000_000.0;
        }
        return times;
    }

    // ── Statistics ───────────────────────────────────────────────────────
    private static double mean(double[] v) {
        double s = 0;
        for (double x : v) s += x;
        return s / v.length;
    }

    private static double stdDev(double[] v) {
        double m = mean(v), s = 0;
        for (double x : v) s += (x - m) * (x - m);
        return Math.sqrt(s / (v.length - 1));
    }

    /**
     * Welch's t-test for two independent samples (unequal variance assumed).
     * t = (mean1 - mean2) / sqrt(s1²/n1 + s2²/n2)
     */
    private static double tTest(double[] a, double[] b) {
        double ma = mean(a), mb = mean(b);
        double sa = stdDev(a), sb = stdDev(b);
        return (ma - mb) / Math.sqrt((sa * sa / a.length) + (sb * sb / b.length));
    }

    // ── Data generators ──────────────────────────────────────────────────
    private static int[] randomData(int n) {
        Random r = new Random(SEED);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(n * 10);
        return a;
    }

    private static int[] sortedData(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    private static int[] reversedData(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    private static int[] nearlySorted(int n) {
        int[] a = sortedData(n);
        Random r = new Random(SEED);
        // Randomly swap 10% of positions
        for (int i = 0; i < n / 10; i++) {
            int x = r.nextInt(n), y = r.nextInt(n);
            int tmp = a[x]; a[x] = a[y]; a[y] = tmp;
        }
        return a;
    }

    private static int[] manyDuplicates(int n) {
        Random r = new Random(SEED);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(10);  // only 10 distinct values
        return a;
    }
}
