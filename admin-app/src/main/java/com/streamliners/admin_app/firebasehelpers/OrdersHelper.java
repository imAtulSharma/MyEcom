package com.streamliners.admin_app.firebasehelpers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrdersHelper {

    /**
     * Get the order from the firebase
     * @param listener listener for the callbacks
     */
    public void getOrders(OnCompleteListener<List<Order>> listener){
        List<Order> orders = new ArrayList<>();

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("orders").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            if (snapshot.exists()){
                                orders.add(snapshot.toObject(Order.class));
                            }
                        }
                        listener.onCompleted(orders);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onFailed(e.toString());
            }
        });
    }
}
