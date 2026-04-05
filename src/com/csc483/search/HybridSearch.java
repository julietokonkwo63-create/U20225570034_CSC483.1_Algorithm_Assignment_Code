package com.csc483.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hybrid search system combining:
 * <ul>
 *   <li>A sorted {@link ArrayList} for O(log n) binary search by productId.</li>
 *   <li>A {@link HashMap} index for O(1) average name lookup.</li>
 * </ul>
 *
 * <p><b>Time-complexity summary:</b>
 * <pre>
 *   searchById(id)     → O(log n)  binary search on sorted list
 *   searchByName(name) → O(1)      HashMap lookup (average case)
 *   addProduct(p)      → O(log n) for position lookup + O(n) for ArrayList insert
 *                        O(1) for HashMap put
 * </pre>
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public class HybridSearch {

    /** Sorted list maintained for binary search by productId */
    private final List<Product> sortedById;

    /** HashMap index: productName (lowercase) → Product */
    private final Map<String, Product> nameIndex;

    // ── Constructor ───────────────────────────────────────────────────────
    /** Creates an empty HybridSearch structure. */
    public HybridSearch() {
        this.sortedById = new ArrayList<>();
        this.nameIndex  = new HashMap<>();
    }

    // ── Build from existing array ─────────────────────────────────────────
    /**
     * Populates the structure from an existing Product array.
     * Sorts the list and builds the name index in one pass.
     *
     * @param products existing product catalogue (null entries are skipped)
     * Time complexity: O(n log n) for sort + O(n) for HashMap inserts
     */
    public void buildIndex(Product[] products) {
        sortedById.clear();
        nameIndex.clear();
        if (products == null) return;
        for (Product p : products) {
            if (p != null) {
                sortedById.add(p);
                nameIndex.put(p.getProductName().toLowerCase(), p);
            }
        }
        // Sort ascending by productId for binary search
        sortedById.sort(Comparator.comparingInt(Product::getProductId));
    }

    // ── Search by ID ──────────────────────────────────────────────────────
    /**
     * Searches for a product by its unique ID using binary search.
     *
     * @param targetId productId to locate
     * @return matching {@link Product} or {@code null}
     * Time complexity: O(log n)
     */
    public Product searchById(int targetId) {
        int low = 0, high = sortedById.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = Integer.compare(sortedById.get(mid).getProductId(), targetId);
            if      (cmp == 0) return sortedById.get(mid);
            else if (cmp  < 0) low  = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // ── Search by Name ────────────────────────────────────────────────────
    /**
     * Searches for a product by name using the HashMap index.
     *
     * @param name product name (case-insensitive)
     * @return matching {@link Product} or {@code null}
     * Time complexity: O(1) average
     */
    public Product searchByName(String name) {
        if (name == null) return null;
        return nameIndex.get(name.toLowerCase());
    }

    // ── Add Product ───────────────────────────────────────────────────────
    /**
     * Adds a new product while maintaining sorted order for binary search.
     * Uses binary search to find the insertion position, then shifts elements.
     *
     * <p>The HashMap is updated in O(1). The ArrayList insert is O(n) in the
     * worst case due to element shifting, but O(log n) to find the position.
     *
     * @param newProduct the product to add (must not be null)
     * @throws IllegalArgumentException if newProduct is null
     */
    public void addProduct(Product newProduct) {
        if (newProduct == null) throw new IllegalArgumentException("Product must not be null.");

        // O(1) average – update name index
        nameIndex.put(newProduct.getProductName().toLowerCase(), newProduct);

        // O(log n) – find correct sorted position using Collections.binarySearch
        int pos = Collections.binarySearch(
                sortedById, newProduct,
                Comparator.comparingInt(Product::getProductId));

        // binarySearch returns -(insertion point) - 1 when not found
        if (pos < 0) pos = -(pos + 1);

        // O(n) – shift elements right and insert (maintains sorted order)
        sortedById.add(pos, newProduct);
    }

    // ── Utility ───────────────────────────────────────────────────────────
    /** @return number of products in the structure */
    public int size() { return sortedById.size(); }

    /** @return a copy of the sorted list (for testing) */
    public List<Product> getSortedList() {
        return new ArrayList<>(sortedById);
    }
}
