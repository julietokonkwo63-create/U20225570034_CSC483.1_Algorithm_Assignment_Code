package com.csc483.search;

/**
 * Represents a product in the TechMart online store catalogue.
 * Implements Comparable to support sorting by productId.
 *
 * @author CSC 483 Student
 * @version 1.0
 */
public class Product implements Comparable<Product> {

    // ── Fields ────────────────────────────────────────────────────────────
    private final int    productId;
    private       String productName;
    private       String category;
    private       double price;
    private       int    stockQuantity;

    // ── Constructor ───────────────────────────────────────────────────────
    /**
     * Constructs a Product with all required attributes.
     *
     * @param productId     unique integer identifier (1 – 200,000)
     * @param productName   human-readable product name
     * @param category      product category (e.g., "Electronics")
     * @param price         unit price in naira (must be >= 0)
     * @param stockQuantity number of units in stock (must be >= 0)
     * @throws IllegalArgumentException if price or stockQuantity is negative
     */
    public Product(int productId, String productName, String category,
                   double price, int stockQuantity) {
        if (price < 0)         throw new IllegalArgumentException("Price cannot be negative.");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.productId     = productId;
        this.productName   = productName;
        this.category      = category;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    /** @return unique product identifier */
    public int    getProductId()      { return productId; }
    /** @return human-readable product name */
    public String getProductName()    { return productName; }
    /** @return product category string */
    public String getCategory()       { return category; }
    /** @return unit price */
    public double getPrice()          { return price; }
    /** @return number of units available */
    public int    getStockQuantity()  { return stockQuantity; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setProductName(String productName) { this.productName = productName; }
    public void setCategory(String category)       { this.category = category; }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }
    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.stockQuantity = stockQuantity;
    }

    // ── Comparable ────────────────────────────────────────────────────────
    /**
     * Natural ordering is by productId (ascending).
     * Required so Arrays.sort() can produce sorted order for binary search.
     */
    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.productId, other.productId);
    }

    // ── Object overrides ─────────────────────────────────────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        return this.productId == ((Product) o).productId;
    }

    @Override
    public int hashCode() { return Integer.hashCode(productId); }

    @Override
    public String toString() {
        return String.format("Product[id=%-6d | name=%-25s | cat=%-12s | price=%8.2f | stock=%4d]",
                productId, productName, category, price, stockQuantity);
    }
}
