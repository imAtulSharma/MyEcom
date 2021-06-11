package com.streamliners.myecom.tmp;

import android.net.Uri;

import com.streamliners.models.Product;
import com.streamliners.models.Variant;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ProductsHelper {
    public List<Product> getProducts(){
        List<Product> products = new ArrayList<>();

        Product apple = new Product("Apple",Uri.parse("android.resource://com.streamliners.myecom/drawable/apple").toString(), 2, 100);


        Product kiwi = new Product("Kiwi", Uri.parse("android.resource://com.streamliners.myecom/drawable/kiwi").toString(), new ArrayList<Variant>(
                Arrays.asList(new Variant("500g", 96), new Variant("1kg", 180))
        ));
        Product banana = new Product("Banana", Uri.parse("android.resource://com.streamliners.myecom/drawable/banane_large").toString(),1,30);

        Product surfExcel = new Product("Surf Excel", Uri.parse("android.resource://com.streamliners.myecom/drawable/surf_excel").toString(), new ArrayList<>(
                Arrays.asList(new Variant("1kg", 120), new Variant("5kg", 500), new Variant("10kg", 900))
        ));

        products.add(apple);
        products.add(banana);
        products.add(kiwi);
        products.add(surfExcel);


        return products;
    }
}
