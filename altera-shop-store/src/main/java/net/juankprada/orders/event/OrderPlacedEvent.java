package net.juankprada.orders.event;


import jakarta.enterprise.context.ApplicationScoped;
import net.juankprada.orders.model.OrderMessage;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;


@ApplicationScoped
public class OrderPlacedEvent {
    private final Logger log = LoggerFactory.getLogger(OrderPlacedEvent.class);

    private final BlockingQueue<OrderMessage> messages = new LinkedBlockingQueue<>();

    public void add(OrderMessage message) {
        messages.add(message);
    }

    @Outgoing("order-placed")
    public CompletionStage<Message<OrderMessage>> send() {

        return CompletableFuture.supplyAsync(() -> {

            try {
                OrderMessage orderMessage = messages.take();
                log.error("Placed order event: " + orderMessage);

                return Message.of(orderMessage);
            } catch (InterruptedException e) {
                log.error("Error polling data from OrderPlacedEvent queue :: {}", e.getMessage());
                throw new RuntimeException(e);
            }

        });
    }

}
