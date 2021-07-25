package com.streamliners.models.models;

import java.util.List;

/**
 * Represents a product
 */
public class Product {
    public int position;
    /**
     * Name and image URL of the product
     */
    public String name, imageURL;

    /**
     * Type of the product <br>
     * Whether it is weight based or variant based
     */
    public int type;

    /**
     * List of {@link Variant}(s) of the product
     */
    public List<Variant> variants;

    /**
     * Minimum quantity to be purchased at least <br>
     * Price of the product of unit kilogram
     */
    public float minQty, pricePerKg;

    public Product(){

    }
    /**
     * Initialize the variant based product
     * @param name      name of the product
     * @param imageURL  URL of the image for the product
     * @param variants  list of the variants of the product
     */
    public Product(String name, String imageURL, List<Variant> variants) {
        this.type = ProductType.TYPE_VARIANT_BASED_PRODUCT;
        this.name = name;
        this.imageURL = imageURL;
        this.variants = variants;
    }

    /**
     * Initialize the weight based product
     * @param name       name of the product
     * @param imageURL   URL of the image for the product
     * @param minQty     minimum quantity must be purchased of the product
     * @param pricePerKg price of the product of 1 kg
     */
    public Product(String name, String imageURL, float minQty, float pricePerKg) {
        this.type = ProductType.TYPE_WEIGHT_BASED_PRODUCT;
        this.name = name;
        this.imageURL = imageURL;
        this.minQty = minQty;
        this.pricePerKg = pricePerKg;
    }

    /**
     * Overriding the method {@link Object#toString()}
     *
     * @return the data of the {@link Product} class with all parameters
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (this.type == ProductType.TYPE_WEIGHT_BASED_PRODUCT) {
            stringBuilder.append("\nWeightBasedProduct { ");
        } else {
            stringBuilder.append("VariantBasedProduct { ");
        }

        stringBuilder.append("name = ").append(this.name);

        if (this.type == ProductType.TYPE_WEIGHT_BASED_PRODUCT) {
            stringBuilder.append(", minQty = ").append(this.minQty);
            stringBuilder.append(", pricePerKg = ").append(this.pricePerKg);
        } else {
            stringBuilder.append(", variants = ").append(this.variants);
        }
        stringBuilder.append(" } ");

        return stringBuilder.toString();
    }
}
