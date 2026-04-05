package com.csc483.sorting;

/**
 * Implements Insertion Sort, Merge Sort, Quick Sort, and Heap Sort
 * with built-in comparison and swap counters for empirical analysis.
 *
 * <p>Each sort method returns a {@link SortResult} containing the
 * sorted array, comparison count, and swap/assignment count.
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public final class SortingAlgorithms {

    private SortingAlgorithms() {}

    // ── Shared mutable counters (reset per sort call) ────────────────────
    private static long comparisons;
    private static long swaps;

    // ════════════════════════════════════════════════════════════════════
    // 1.  INSERTION SORT
    //     Best:    O(n)      – already sorted
    //     Average: O(n²)
    //     Worst:   O(n²)     – reverse sorted
    //     Space:   O(1)      in-place, stable
    // ════════════════════════════════════════════════════════════════════
    /**
     * Sorts a copy of {@code arr} using insertion sort.
     *
     * @param arr       input array (not modified)
     * @param dataType  label for the benchmark result
     * @param timeMs    pre-measured wall time (pass 0 to skip)
     * @return {@link long[]} where [0]=comparisons, [1]=swaps, [2..n+1]=sorted data
     */
    public static int[] insertionSort(int[] arr) {
        int[] a = arr.clone();
        comparisons = 0; swaps = 0;

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j   = i - 1;
            while (j >= 0 && ++comparisons > 0 && a[j] > key) {
                a[j + 1] = a[j];   // shift right
                swaps++;
                j--;
            }
            if (j + 1 != i) swaps++;  // count the final key placement
            a[j + 1] = key;
        }
        return a;
    }

    public static long getComparisons() { return comparisons; }
    public static long getSwaps()       { return swaps; }

    // ════════════════════════════════════════════════════════════════════
    // 2.  MERGE SORT
    //     Best / Average / Worst: O(n log n)   stable
    //     Space: O(n)             NOT in-place
    // ════════════════════════════════════════════════════════════════════
    public static int[] mergeSort(int[] arr) {
        int[] a = arr.clone();
        comparisons = 0; swaps = 0;
        mergeSortHelper(a, 0, a.length - 1);
        return a;
    }

    private static void mergeSortHelper(int[] a, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortHelper(a, left, mid);
        mergeSortHelper(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    private static void merge(int[] a, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];

        // Copy to temporary arrays (each copy counts as an assignment)
        for (int i = 0; i < n1; i++) { L[i] = a[left + i];  swaps++; }
        for (int j = 0; j < n2; j++) { R[j] = a[mid + 1 + j]; swaps++; }

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            comparisons++;
            if (L[i] <= R[j]) { a[k++] = L[i++]; swaps++; }
            else               { a[k++] = R[j++]; swaps++; }
        }
        while (i < n1) { a[k++] = L[i++]; swaps++; }
        while (j < n2) { a[k++] = R[j++]; swaps++; }
    }

    // ════════════════════════════════════════════════════════════════════
    // 3.  QUICK SORT  (last-element pivot)
    //     Best / Average: O(n log n)   NOT stable
    //     Worst: O(n²)                 – already sorted with last-element pivot
    //     Space: O(log n) stack        in-place
    // ════════════════════════════════════════════════════════════════════
    public static int[] quickSort(int[] arr) {
        int[] a = arr.clone();
        comparisons = 0; swaps = 0;
        quickSortHelper(a, 0, a.length - 1);
        return a;
    }

    private static void quickSortHelper(int[] a, int low, int high) {
        if (low < high) {
            int pi = partition(a, low, high);
            quickSortHelper(a, low, pi - 1);
            quickSortHelper(a, pi + 1, high);
        }
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high];
        int i     = low - 1;
        for (int j = low; j < high; j++) {
            comparisons++;
            if (a[j] <= pivot) {
                i++;
                int tmp = a[i]; a[i] = a[j]; a[j] = tmp;  // swap
                swaps++;
            }
        }
        // Place pivot in correct position
        int tmp = a[i + 1]; a[i + 1] = a[high]; a[high] = tmp;
        swaps++;
        return i + 1;
    }

    // ════════════════════════════════════════════════════════════════════
    // 4.  HEAP SORT
    //     Best / Average / Worst: O(n log n)   NOT stable
    //     Space: O(1)                           in-place
    // ════════════════════════════════════════════════════════════════════
    public static int[] heapSort(int[] arr) {
        int[] a = arr.clone();
        comparisons = 0; swaps = 0;
        int n = a.length;

        // Phase 1: Build max-heap (heapify bottom-up)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(a, n, i);
        }

        // Phase 2: Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            int tmp = a[0]; a[0] = a[i]; a[i] = tmp;  // move root to end
            swaps++;
            heapify(a, i, 0);
        }
        return a;
    }

    private static void heapify(int[] a, int n, int root) {
        int largest = root;
        int left    = 2 * root + 1;
        int right   = 2 * root + 2;

        if (left < n)  { comparisons++; if (a[left]  > a[largest]) largest = left; }
        if (right < n) { comparisons++; if (a[right] > a[largest]) largest = right; }

        if (largest != root) {
            int tmp = a[root]; a[root] = a[largest]; a[largest] = tmp;
            swaps++;
            heapify(a, n, largest);   // recurse on affected sub-tree
        }
    }
}
