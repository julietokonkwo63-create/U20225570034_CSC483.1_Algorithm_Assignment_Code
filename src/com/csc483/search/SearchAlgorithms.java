package com.csc483.search;

/**
 * Provides sequential search, binary search, and name search operations
 * on an array of {@link Product} objects.
 *
 * <p>Complexity summary:
 * <ul>
 *   <li>sequentialSearchById  – O(n)      best O(1)</li>
 *   <li>binarySearchById      – O(log n)  best O(1)  REQUIRES sorted array</li>
 *   <li>searchByName          – O(n)      best O(1)</li>
 * </ul>
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public final class SearchAlgorithms {

    // Prevent instantiation – utility class
    private SearchAlgorithms() {}

    // ── 1. Sequential Search by ID ────────────────────────────────────────
    /**
     * Searches for a product by ID using linear (sequential) scan.
     * Works on unsorted or sorted arrays.
     *
     * @param products  array of Product objects (may be null)
     * @param targetId  the productId to locate
     * @return matching {@link Product} or {@code null} if not found
     * Time complexity:  O(n)  –  worst/average
     *                   O(1)  –  best (target at index 0)
     */
    public static Product sequentialSearchById(Product[] products, int targetId) {
        if (products == null) return null;
        for (Product p : products) {          // examine every element in order
            if (p != null && p.getProductId() == targetId) {
                return p;                     // found – return immediately
            }
        }
        return null;                          // not found
    }

    /**
     * Overload that also counts comparisons (for benchmarking).
     * Returns a two-element array: [result index, comparison count].
     * Index -1 means not found.
     */
    public static long[] sequentialSearchCount(Product[] products, int targetId) {
        long comparisons = 0;
        if (products == null) return new long[]{-1, 0};
        for (int i = 0; i < products.length; i++) {
            comparisons++;
            if (products[i] != null && products[i].getProductId() == targetId) {
                return new long[]{i, comparisons};
            }
        }
        return new long[]{-1, comparisons};
    }

    // ── 2. Binary Search by ID ────────────────────────────────────────────
    /**
     * Searches for a product by ID using iterative binary search.
     *
     * <p><b>Precondition:</b> The {@code products} array MUST be sorted in
     * ascending order of {@code productId}. If this precondition is violated,
     * results are undefined.
     *
     * @param products  array sorted ascending by productId
     * @param targetId  the productId to locate
     * @return matching {@link Product} or {@code null} if not found
     * Time complexity:  O(log n)  –  worst/average
     *                   O(1)      –  best (target at midpoint on first probe)
     */
    public static Product binarySearchById(Product[] products, int targetId) {
        if (products == null) return null;
        int low = 0, high = products.length - 1;

        while (low <= high) {
            // Use (low + (high-low)/2) to avoid integer overflow
            int mid = low + (high - low) / 2;

            if (products[mid] == null) { high = mid - 1; continue; }

            int cmp = Integer.compare(products[mid].getProductId(), targetId);

            if      (cmp == 0) return products[mid];   // found
            else if (cmp  < 0) low  = mid + 1;         // target in right half
            else               high = mid - 1;         // target in left half
        }
        return null;                                   // not found
    }

    /**
     * Overload that also counts comparisons.
     * Returns [result index (-1 = not found), comparison count].
     */
    public static long[] binarySearchCount(Product[] products, int targetId) {
        if (products == null) return new long[]{-1, 0};
        int low = 0, high = products.length - 1;
        long comparisons = 0;

        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            if (products[mid] == null) { high = mid - 1; continue; }
            int cmp = Integer.compare(products[mid].getProductId(), targetId);
            if      (cmp == 0) return new long[]{mid, comparisons};
            else if (cmp  < 0) low  = mid + 1;
            else               high = mid - 1;
        }
        return new long[]{-1, comparisons};
    }

    // ── 3. Sequential Search by Name ─────────────────────────────────────
    /**
     * Case-insensitive search for a product by name.
     * Sequential (linear) because the name field is not sorted.
     *
     * @param products   array of Product objects
     * @param targetName product name to match (case-insensitive)
     * @return first matching {@link Product} or {@code null}
     * Time complexity:  O(n)
     */
    public static Product searchByName(Product[] products, String targetName) {
        if (products == null || targetName == null) return null;
        for (Product p : products) {
            if (p != null && p.getProductName().equalsIgnoreCase(targetName)) {
                return p;
            }
        }
        return null;
    }
}
