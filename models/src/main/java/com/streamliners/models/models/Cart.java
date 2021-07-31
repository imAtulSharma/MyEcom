package com.streamliners.models.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * represents cart with having cart items as its members
 */
public class Cart implements Serializable {

    /**
     * Total amount of all items in cart
     */
    public float totalAmount;

    /**
     * To number of items in cart
     */
    public int numberOfItems;

    /**
     * Map of all items in cart
     */
    public HashMap<String, CartItem> cartItems = new HashMap<>();

    /**
     * To add/edit weight based product in the cart
     *
     * @param product product to be added
     * @param qty     quantity of the product
     */
    public void add(Product product, float qty) {

        // if the item already exists in the cart
        if (cartItems.containsKey(product.name)) {
            totalAmount -= cartItems.get(product.name).cost();
            cartItems.get(product.name).qty = qty;
        } else {
            CartItem cartItem = new CartItem(product.name, product.pricePerKg, qty);
            cartItems.put(product.name, cartItem);

            // incrementing number of items
            numberOfItems++;
        }

        // Adding amount of the item to the total amount
        totalAmount += cartItems.get(product.name).cost();
    }

    /**
     * To add/edit variant based product in the cart
     *
     * @param product product to be added
     * @param variant which variant of the product to be added in the cart
     */
    public void add(Product product, Variant variant) {

        // make a string for the key by using concatenation
        String key = product.name + " " + variant.name;

        // if item already exists in the cart
        if (cartItems.containsKey(key)) {
            cartItems.get(key).qty++;
        } else {
            CartItem cartItem =  new CartItem(product.name, variant.price, 1);
            cartItems.put(key, cartItem);
            numberOfItems++;
        }

        // Update cart summary
        totalAmount += variant.price;
    }

    /**
     * To remove the product completely
     *
     * @param product product to be removed
     */
    public void remove(Product product) {
        if (product.type == ProductType.TYPE_WEIGHT_BASED_PRODUCT) {
            removeWeightBasedProduct(product);
        } else {
            removeAllVariantsOfVariantBasedProduct(product);
        }
    }

    /**
     * To remove the weight based product completely
     *
     * @param product weight based product to be removed
     */
    private void removeWeightBasedProduct(Product product) {
        // update cart details
        totalAmount -= cartItems.get(product.name).cost();
        numberOfItems--;

        // removing from the cart items
        cartItems.remove(product.name);
    }

    /**
     * To remove the variant based product completely
     *
     * @param product variant based product to be removed
     */
    public void removeAllVariantsOfVariantBasedProduct(Product product) {
        for (Variant variant : product.variants) {
            String key = product.name + " " + variant.name;

            if (cartItems.containsKey(key)) {
                // update cart details
                totalAmount -= cartItems.get(key).cost();
                numberOfItems--;

                // removing from the  cart items
                cartItems.remove(key);
            }
        }
    }

    /**
     * To decrease the quantity of the variant based product
     *
     * @param product product of which the quantity is to be decreased
     * @param variant variant of the product to be decreased
     */
    public void decrement(Product product, Variant variant) {
        String key = product.name + " " + variant.name;

        // Update quantity
        cartItems.get(key).qty--;

        // update cart details
        totalAmount -= variant.price;
        numberOfItems--;

        // removing from the  cart items
        if (cartItems.get(key).qty == 0)
            cartItems.remove(key);
    }

    public boolean isEmpty(){
        return numberOfItems == 0;
    }
    /**
     * Overriding the {@link Object#toString()} method
     *
     * @return return string in the formatted string
     */
    @Override
    public String toString() {
        return cartItems.values() +
                String.format("\ntotal %d items (Rs. %.2f)", numberOfItems, totalAmount);
    }
}
