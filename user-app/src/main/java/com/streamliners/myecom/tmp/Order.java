package com.streamliners.myecom.tmp;

import com.google.android.gms.maps.model.LatLng;
import com.streamliners.models.models.Cart;
import com.streamliners.models.models.CartItem;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Represents order class
 */
public class Order {

    /**
     * Represents status of the order
     */
    public class OrderStatus {
        public final static int WAITING = 0;
        public final static int ACCEPTED = 1;
        public final static int DISPATCHED = 2;
        public final static int DELIVERED = 3;
        public final static int DECLINED = -1;
        public final static int CANCELLED = -2;
    }

    public Timestamp createdTime;
    public int status, noOfItems;
    public float subTotal;
    public HashMap<String, CartItem> cartItems;
    public String userName, userPhoneNo, userAddress, userDeviceToken;
    public LatLng userCoordinates;

    public Order(Cart cart, String userName, String userPhoneNo, String userAddress, String userDeviceToken, LatLng userCoordinates) {
        this.cartItems = cart.cartItems;
        this.noOfItems = cart.numberOfItems;
        this.subTotal = cart.totalAmount;
        this.userName = userName;
        this.userPhoneNo = userPhoneNo;
        this.userAddress = userAddress;
        this.userDeviceToken = userDeviceToken;
        this.userCoordinates = userCoordinates;
    }
}
