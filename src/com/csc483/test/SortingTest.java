package com.csc483.test;

import com.csc483.sorting.SortingAlgorithms;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for SortingAlgorithms.
 * Verifies correctness across all four algorithms on multiple data types.
 *
 * @author CSC 483 Student
 */
@DisplayName("Sorting Algorithm Tests")
class SortingTest {

    // Shared test cases as static factory
    static Stream<int[]> testArrays() {
        return Stream.of(
            new int[]{5, 3, 8, 1, 9, 2},           // random small
            new int[]{1, 2, 3, 4, 5},               // already sorted
            new int[]{5, 4, 3, 2, 1},               // reversed
            new int[]{1},                            // single element
            new int[]{},                             // empty
            new int[]{7, 7, 7, 7},                  // all duplicates
            new int[]{-3, 0, 5, -1, 8, -7}          // negatives
        );
    }

    // Helper: check array is sorted ascending
    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i] < a[i-1]) return false;
        return true;
    }

    // ── Insertion Sort ────────────────────────────────────────────────────
    @ParameterizedTest(name = "InsertionSort – {0}")
    @MethodSource("testArrays")
    @DisplayName("Insertion Sort produces sorted output")
    void insertionSortCorrect(int[] input) {
        int[] result = SortingAlgorithms.insertionSort(input);
        assertTrue(isSorted(result));
    }

    @Test @DisplayName("Insertion Sort does not modify original array")
    void insertionSortImmutable() {
        int[] original = {3, 1, 4, 1, 5};
        int[] copy = original.clone();
        SortingAlgorithms.insertionSort(original);
        assertArrayEquals(copy, original);
    }

    @Test @DisplayName("Insertion Sort – best case O(n): nearly-zero comparisons on sorted")
    void insertionSortBestCase() {
        int[] sorted = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        SortingAlgorithms.insertionSort(sorted);
        // On sorted input, outer loop runs n-1 times, inner while never enters → comparisons ≈ n-1
        assertTrue(SortingAlgorithms.getComparisons() <= sorted.length - 1 + 1);
    }

    // ── Merge Sort ────────────────────────────────────────────────────────
    @ParameterizedTest(name = "MergeSort – {0}")
    @MethodSource("testArrays")
    @DisplayName("Merge Sort produces sorted output")
    void mergeSortCorrect(int[] input) {
        int[] result = SortingAlgorithms.mergeSort(input);
        assertTrue(isSorted(result));
    }

    @Test @DisplayName("Merge Sort is stable – equal elements maintain relative order")
    void mergeSortStable() {
        // Use a large array; if result matches Arrays.sort output it is correct
        Random r = new Random(42);
        int[] data = new int[1000];
        for (int i = 0; i < 1000; i++) data[i] = r.nextInt(50);
        int[] expected = data.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, SortingAlgorithms.mergeSort(data));
    }

    @Test @DisplayName("Merge Sort does not modify original array")
    void mergeSortImmutable() {
        int[] original = {9, 3, 7, 1};
        int[] copy = original.clone();
        SortingAlgorithms.mergeSort(original);
        assertArrayEquals(copy, original);
    }

    // ── Quick Sort ────────────────────────────────────────────────────────
    @ParameterizedTest(name = "QuickSort – {0}")
    @MethodSource("testArrays")
    @DisplayName("Quick Sort produces sorted output")
    void quickSortCorrect(int[] input) {
        int[] result = SortingAlgorithms.quickSort(input);
        assertTrue(isSorted(result));
    }

    @Test @DisplayName("Quick Sort handles large random array correctly")
    void quickSortLarge() {
        Random r = new Random(99);
        int[] data = new int[10_000];
        for (int i = 0; i < data.length; i++) data[i] = r.nextInt(100_000);
        int[] expected = data.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, SortingAlgorithms.quickSort(data));
    }

    // ── Heap Sort ─────────────────────────────────────────────────────────
    @ParameterizedTest(name = "HeapSort – {0}")
    @MethodSource("testArrays")
    @DisplayName("Heap Sort produces sorted output")
    void heapSortCorrect(int[] input) {
        int[] result = SortingAlgorithms.heapSort(input);
        assertTrue(isSorted(result));
    }

    @Test @DisplayName("All algorithms produce identical output on same input")
    void allAlgorithmsAgree() {
        Random r = new Random(777);
        int[] data = new int[500];
        for (int i = 0; i < 500; i++) data[i] = r.nextInt(10_000);

        int[] refResult = data.clone();
        Arrays.sort(refResult);

        assertArrayEquals(refResult, SortingAlgorithms.insertionSort(data), "Insertion mismatch");
        assertArrayEquals(refResult, SortingAlgorithms.mergeSort(data),     "Merge mismatch");
        assertArrayEquals(refResult, SortingAlgorithms.quickSort(data),     "Quick mismatch");
        assertArrayEquals(refResult, SortingAlgorithms.heapSort(data),      "Heap mismatch");
    }
}
