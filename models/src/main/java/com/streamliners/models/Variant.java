package com.streamliners.models;

/**
 * Represents a variant
 */
public class Variant {
    /**
     * Name of the variant
     */
    public String name;

    /**
     * Price of the variant
     */
    public float price;

    /**
     * Initialize the product with . . .
     *
     * @param name  name of the variant
     * @param price price of the variant
     */
    public Variant(String name, float price) {
        this.name = name;
        this.price = price;
    }

    /**
     * Overriding the method {@link Object#toString()}
     *
     * @return the data of the {@link Variant} class with all parameters in a specific format
     * format - "{name} @ Rs. {price}"
     */
    @Override
    public String toString() {
        return String.format("%s @ Rs. %.2f", name, price);
    }
}
