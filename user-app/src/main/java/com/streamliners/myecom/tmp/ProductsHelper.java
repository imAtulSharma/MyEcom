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

        Product kiwi = new Product("Kiwi", Uri.parse("android.resource://com.streamliners.myecom/drawable/kiwi").toString(), new ArrayList<Variant>(
                Arrays.asList(new Variant("500g", 96), new Variant("1kg", 180))
        ));

        Product apple = new Product("Apple",Uri.parse("android.resource://com.streamliners.myecom/drawable/apple").toString(), (float) 0.55, 100);

        Product banana = new Product("Banana", Uri.parse("android.resource://com.streamliners.myecom/drawable/banane_large").toString(),1,30);

        Product surfExcel = new Product("Surf Excel", Uri.parse("android.resource://com.streamliners.myecom/drawable/surf_excel").toString(), new ArrayList<>(
                Arrays.asList(new Variant("1kg", 120), new Variant("5kg", 500), new Variant("10kg", 900))
        ));

        Product milk = new Product("Milk", Uri.parse("android.resource://com.streamliners.myecom/drawable/milk").toString(), new ArrayList<>(
                Arrays.asList(new Variant("500 ml", 35), new Variant("1 litre", 60))
        ));

        Product grapes = new Product("Grapes",Uri.parse("android.resource://com.streamliners.myecom/drawable/grapes").toString(), (float) 1, 40);

        Product mango = new Product("Mango",Uri.parse("android.resource://com.streamliners.myecom/drawable/mango").toString(), (float) 0, 50);


        Product paneer = new Product("Paneer", Uri.parse("android.resource://com.streamliners.myecom/drawable/paneer").toString(), new ArrayList<Variant>(
                Arrays.asList(new Variant("200g", 80), new Variant("400g", 150))
        ));
        Product orange = new Product("Orange", Uri.parse("android.resource://com.streamliners.myecom/drawable/orange").toString(),3,60);

        Product sugar = new Product("Sugar", Uri.parse("android.resource://com.streamliners.myecom/drawable/sugar").toString(), new ArrayList<>(
                Arrays.asList(new Variant("1kg", 800), new Variant("5kg", 500))
        ));

        Product peach = new Product("Peach",Uri.parse("android.resource://com.streamliners.myecom/drawable/peach").toString(), (float) 1, 80);

        Product aashirvaad = new Product("Aashirvaad Aata", Uri.parse("android.resource://com.streamliners.myecom/drawable/aashirvaad").toString(), new ArrayList<>(
                Arrays.asList(new Variant("1kg", 100), new Variant("5kg",480))
        ));

        products.add(kiwi);
        products.add(apple);
        products.add(banana);
        products.add(surfExcel);
        products.add(milk);
        products.add(grapes);
        products.add(paneer);
        products.add(mango);
        products.add(orange);
        products.add(sugar);
        products.add(peach);
        products.add(aashirvaad);


        return products;
    }
}
