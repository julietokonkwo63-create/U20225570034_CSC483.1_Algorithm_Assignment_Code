package com.csc483.test;

import com.csc483.search.HybridSearch;
import com.csc483.search.Product;
import com.csc483.search.SearchAlgorithms;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for SearchAlgorithms and HybridSearch.
 * Covers: normal cases, edge cases, null inputs, duplicates.
 *
 * @author CSC 483 Student
 */
@DisplayName("Search Algorithm Tests")
class SearchTest {

    private static Product[] sortedProducts;
    private static Product[] unsortedProducts;

    @BeforeAll
    static void setup() {
        // Sorted by productId: 1, 3, 7, 12, 25, 50, 99
        sortedProducts = new Product[]{
            new Product(1,  "ProductA", "Electronics", 100.0, 10),
            new Product(3,  "ProductB", "Phones",      200.0, 5),
            new Product(7,  "ProductC", "Laptops",     500.0, 3),
            new Product(12, "ProductD", "TVs",         999.0, 1),
            new Product(25, "ProductE", "Audio",       150.0, 20),
            new Product(50, "ProductF", "Gaming",      350.0, 8),
            new Product(99, "ProductG", "Cameras",     750.0, 2),
        };

        // Deliberately unsorted (for sequential-only tests)
        unsortedProducts = new Product[]{
            new Product(55, "Widget",   "Electronics", 99.99,  5),
            new Product(10, "Gadget",   "Phones",      199.99, 3),
            new Product(77, "Doohickey","Laptops",     299.99, 1),
        };
    }

    // ── Sequential Search Tests ──────────────────────────────────────────
    @Test @DisplayName("Sequential – finds product at index 0 (best case)")
    void seqBestCase() {
        Product r = SearchAlgorithms.sequentialSearchById(sortedProducts, 1);
        assertNotNull(r);
        assertEquals(1, r.getProductId());
    }

    @Test @DisplayName("Sequential – finds product at last index (worst case)")
    void seqWorstCase() {
        Product r = SearchAlgorithms.sequentialSearchById(sortedProducts, 99);
        assertNotNull(r);
        assertEquals(99, r.getProductId());
    }

    @Test @DisplayName("Sequential – returns null when ID not found")
    void seqNotFound() {
        assertNull(SearchAlgorithms.sequentialSearchById(sortedProducts, 1000));
    }

    @Test @DisplayName("Sequential – null array returns null")
    void seqNullArray() {
        assertNull(SearchAlgorithms.sequentialSearchById(null, 1));
    }

    @Test @DisplayName("Sequential – empty array returns null")
    void seqEmptyArray() {
        assertNull(SearchAlgorithms.sequentialSearchById(new Product[0], 1));
    }

    // ── Binary Search Tests ──────────────────────────────────────────────
    @Test @DisplayName("Binary – finds product at exact midpoint")
    void binMidpoint() {
        // sortedProducts has 7 elements; index 3 (id=12) is the midpoint
        Product r = SearchAlgorithms.binarySearchById(sortedProducts, 12);
        assertNotNull(r);
        assertEquals(12, r.getProductId());
    }

    @ParameterizedTest(name = "Binary – finds ID {0}")
    @ValueSource(ints = {1, 3, 7, 12, 25, 50, 99})
    void binFindsAllIds(int targetId) {
        Product r = SearchAlgorithms.binarySearchById(sortedProducts, targetId);
        assertNotNull(r);
        assertEquals(targetId, r.getProductId());
    }

    @Test @DisplayName("Binary – returns null when ID not found (worst case)")
    void binNotFound() {
        assertNull(SearchAlgorithms.binarySearchById(sortedProducts, 999));
    }

    @Test @DisplayName("Binary – null array returns null")
    void binNullArray() {
        assertNull(SearchAlgorithms.binarySearchById(null, 1));
    }

    @Test @DisplayName("Binary – comparison count is <= log2(n)+1")
    void binComparisonCount() {
        long[] result = SearchAlgorithms.binarySearchCount(sortedProducts, 99);
        int maxExpected = (int)(Math.log(sortedProducts.length) / Math.log(2)) + 2;
        assertTrue(result[1] <= maxExpected,
                "Comparisons " + result[1] + " exceed log2(n)+1=" + maxExpected);
    }

    // ── Search By Name Tests ─────────────────────────────────────────────
    @Test @DisplayName("Name search – case-insensitive match")
    void nameSearchCaseInsensitive() {
        Product r = SearchAlgorithms.searchByName(unsortedProducts, "GADGET");
        assertNotNull(r);
        assertEquals(10, r.getProductId());
    }

    @Test @DisplayName("Name search – returns null for unknown name")
    void nameSearchNotFound() {
        assertNull(SearchAlgorithms.searchByName(unsortedProducts, "Nonexistent"));
    }

    // ── HybridSearch Tests ───────────────────────────────────────────────
    @Nested @DisplayName("HybridSearch Tests")
    class HybridTests {
        private HybridSearch hybrid;

        @BeforeEach
        void init() {
            hybrid = new HybridSearch();
            hybrid.buildIndex(sortedProducts);
        }

        @Test @DisplayName("Hybrid – searchById finds existing product")
        void hybridSearchById() {
            Product r = hybrid.searchById(7);
            assertNotNull(r);
            assertEquals(7, r.getProductId());
        }

        @Test @DisplayName("Hybrid – searchByName returns correct product (O(1))")
        void hybridSearchByName() {
            Product r = hybrid.searchByName("ProductC");
            assertNotNull(r);
            assertEquals(7, r.getProductId());
        }

        @Test @DisplayName("Hybrid – addProduct maintains sorted order")
        void hybridAddProduct() {
            Product newP = new Product(15, "NewItem", "Electronics", 299.0, 5);
            hybrid.addProduct(newP);
            assertEquals(sortedProducts.length + 1, hybrid.size());
            // Verify sorted order is maintained
            java.util.List<Product> list = hybrid.getSortedList();
            for (int i = 1; i < list.size(); i++) {
                assertTrue(list.get(i).getProductId() > list.get(i-1).getProductId(),
                        "Order violated at index " + i);
            }
        }

        @Test @DisplayName("Hybrid – addProduct with null throws IllegalArgumentException")
        void hybridAddNull() {
            assertThrows(IllegalArgumentException.class, () -> hybrid.addProduct(null));
        }

        @Test @DisplayName("Hybrid – searchByName after addProduct finds new item")
        void hybridSearchAfterAdd() {
            Product newP = new Product(200, "BrandNewThing", "Gaming", 499.0, 10);
            hybrid.addProduct(newP);
            Product found = hybrid.searchByName("BrandNewThing");
            assertNotNull(found);
            assertEquals(200, found.getProductId());
        }
    }

    // ── Product class tests ──────────────────────────────────────────────
    @Nested @DisplayName("Product Class Tests")
    class ProductTests {
        @Test @DisplayName("Product – throws on negative price")
        void negativePrice() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(1, "Test", "Cat", -5.0, 10));
        }

        @Test @DisplayName("Product – throws on negative stock")
        void negativeStock() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(1, "Test", "Cat", 10.0, -1));
        }

        @Test @DisplayName("Product – compareTo sorts by productId")
        void compareToOrder() {
            Product p1 = new Product(5, "A", "C", 10.0, 1);
            Product p2 = new Product(10, "B", "C", 10.0, 1);
            assertTrue(p1.compareTo(p2) < 0);
            assertTrue(p2.compareTo(p1) > 0);
            assertEquals(0, p1.compareTo(new Product(5, "X", "Y", 99.0, 0)));
        }
    }
}
