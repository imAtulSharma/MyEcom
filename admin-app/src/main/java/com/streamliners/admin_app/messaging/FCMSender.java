package com.streamliners.admin_app.messaging;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

/**
 * Represents the Firebase cloud message sender
 */
public class FCMSender {
    /**
     * URL where the post request is to happen
     */
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send",
            AUTHENTICATION_KEY = "authentication_key";

    /**
     * Sends the request to the server
     * @param message message of the request
     * @param authenticationKey authentication key to send the successful request
     * @param listener listener to handle call backs
     */
    public void send(String message, String authenticationKey, Callback listener) {
        // Request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), message);

        // Building the request
        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", authenticationKey)
                .post(requestBody)
                .build();

        // Make call to the server
        Call call = new OkHttpClient().newCall(request);
        call.enqueue(listener);
    }
}
