package com.streamliners.admin_app.messaging;

/**
 * Represents the message builder class to build the messages
 */
public class MessageBuilder {
    /**
     * Default formats of the message
     */
    public static final String NEW_ORDER_FORMAT =
            "{\n" +
                "\"to\":\"%s\",\n" +
                "\"notification\": {\n" +
                    "\"title\": \"Order status!\",\n" +
                    "\"body\": \"Your order is %s\",\n" +
                    "\"icon\": \"ic_order\",\n" +
                    "\"tag\": \"%s\"\n" +
                "}}\n";
            //     "\"data\": {\n" +
            //         "\"orderId\":\"sd57gs5g4g\"\n" +
            //     "}\n" +
            // "}";

    /**
     * Builds the message
     * @param token token of the user
     * @param status status of the order
     * @param orderId ID of the order to update the same notification
     * @return message built
     */
    public static String buildNewOrderMessage(String token, String status, String orderId) {
        return String.format(NEW_ORDER_FORMAT, token, status, orderId);
    }
}
