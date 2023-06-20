package net.juankprada.config;

import net.juankprada.inventory.model.OrderMessage;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class OrderMessageSerializer implements Serializer<OrderMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, OrderMessage data) {
        try {
            if (data == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            System.out.println("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing OrderMessage to byte[]");
        }
    }
}
