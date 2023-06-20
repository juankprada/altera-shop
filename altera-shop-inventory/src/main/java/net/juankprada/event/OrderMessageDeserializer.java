package net.juankprada.event;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import net.juankprada.inventory.model.OrderMessage;

public class OrderMessageDeserializer extends ObjectMapperDeserializer<OrderMessage> {

    public OrderMessageDeserializer() {
        super(OrderMessage.class);
    }
}
