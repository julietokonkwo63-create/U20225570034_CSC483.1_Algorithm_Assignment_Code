package com.csc483.sorting;

/**
 * Immutable container for sorting benchmark results.
 * Holds execution time, comparison count, and swap/assignment count.
 *
 * @author CSC 483 Student
 */
public class SortResult {
    public final String algorithm;
    public final String dataType;
    public final int    inputSize;
    public final double timeMs;
    public final long   comparisons;
    public final long   swaps;        // "N/A" represented as -1 for Merge Sort

    public SortResult(String algorithm, String dataType, int inputSize,
                      double timeMs, long comparisons, long swaps) {
        this.algorithm  = algorithm;
        this.dataType   = dataType;
        this.inputSize  = inputSize;
        this.timeMs     = timeMs;
        this.comparisons = comparisons;
        this.swaps      = swaps;
    }

    @Override
    public String toString() {
        String swapStr = (swaps < 0) ? "       N/A" : String.format("%,10d", swaps);
        return String.format("%-12s %-16s %8d  %10.3f  %,12d  %s",
                algorithm, dataType, inputSize, timeMs, comparisons, swapStr);
    }
}
