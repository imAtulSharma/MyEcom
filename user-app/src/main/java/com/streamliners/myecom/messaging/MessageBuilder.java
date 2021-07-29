package com.streamliners.myecom.messaging;

/**
 * Represents the message builder class to build the messages
 */
public class MessageBuilder {
    /**
     * Default format of the message
     */
    static final String MSG_FORMAT =
            "{\n" +
                "\"to\":\"/topics/admin\",\n" +
                "\"notification\": {\n" +
                    "\"title\": \"New order received!\",\n" +
                    "\"body\": \"%s ordered %d items worth Rs. %d\",\n" +
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
    public static String buildNewOrderMessage(String userName, int noOfItems, int total) {
        return String.format(MSG_FORMAT, userName, noOfItems, total);
    }
}
