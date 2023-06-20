package net.juankprada.config;

import net.juankprada.orders.model.OrderMessage;
import org.apache.kafka.common.serialization.Deserializer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class OrderMessageDeserializer implements Deserializer {

    @Override
    public OrderMessage deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        OrderMessage productPrice = null;
        try {
            productPrice = mapper.readValue(arg1, OrderMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productPrice;
    }
}
