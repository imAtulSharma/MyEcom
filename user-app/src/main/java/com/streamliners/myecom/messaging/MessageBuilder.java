package com.streamliners.myecom.messaging;

/**
 * Represents the message builder class to build the messages
 */
public class MessageBuilder {
    /**
     * Default formats of the message
     */
    public static final String NEW_ORDER_FORMAT =
            "{\n" +
                "\"to\":\"/topics/admin\",\n" +
                "\"notification\": {\n" +
                    "\"title\": \"New order received!\",\n" +
                    "\"body\": \"%s ordered %d items worth Rs. %d\",\n" +
                    "\"icon\": \"ic_order\",\n" +
                    "\"tag\": \"%s\"\n" +
                "}}\n";
            //     "\"data\": {\n" +
            //         "\"orderId\":\"sd57gs5g4g\"\n" +
            //     "}\n" +
            // "}";

    public static final String ORDER_CANCEL_FORMAT =
            "{\n" +
                    "\"to\":\"/topics/admin\",\n" +
                    "\"notification\": {\n" +
                        "\"title\": \"Order cancelled!\",\n" +
                        "\"body\": \"%s cancelled order worth Rs. %d\",\n" +
                        "\"icon\": \"ic_order\",\n" +
                        "\"tag\": \"%s\"\n" +
                    "}}\n";

    /**
     * Builds the message
     * @param userName name of the user/customer
     * @param noOfItems no of items in the order
     * @param total total amount of the order
     * @return message built
     */
    public static String buildNewOrderMessage(String format, String orderId, String userName, int noOfItems, int total) {
        if (format.equals(NEW_ORDER_FORMAT))
            return String.format(format, userName, noOfItems, total, orderId);

        return String.format(format, userName, total, orderId);
    }
}
