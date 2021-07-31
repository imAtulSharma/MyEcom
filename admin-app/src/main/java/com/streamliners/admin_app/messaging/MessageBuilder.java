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
                    "\"icon\": \"ic_order\"\n" +
                "}}\n";
            //     "\"data\": {\n" +
            //         "\"orderId\":\"sd57gs5g4g\"\n" +
            //     "}\n" +
            // "}";

    /**
     * Builds the message
     * @param userName name of the user/customer
     * @param noOfItems no of items in the order
     * @param total total amount of the order
     * @return message built
     */
    public static String buildNewOrderMessage(String token, String status) {
        return String.format(NEW_ORDER_FORMAT, token, status);
    }
}
