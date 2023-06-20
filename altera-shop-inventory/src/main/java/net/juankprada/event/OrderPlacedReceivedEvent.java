package net.juankprada.event;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.annotations.Merge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.juankprada.exception.OrderQuantityException;
import net.juankprada.inventory.InventoryService;
import net.juankprada.inventory.model.OrderMessage;
import net.juankprada.sales.SalesService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


@ApplicationScoped
public class OrderPlacedReceivedEvent {

    private Logger log = LoggerFactory.getLogger(OrderPlacedReceivedEvent.class);

    private ExecutorService executor;
    private BlockingQueue<OrderMessage> messages;

    @Inject
    private InventoryService itemService;

    @Inject
    private SalesService salesService;

    void startup(@Observes StartupEvent event) {
        log.info("========> OrderPlacedReceivedEvent startup");

        messages = new LinkedBlockingQueue<>();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        executor.scheduleAtFixedRate(() -> {
            if (messages.size() > 0) {
                log.info("====> purchased products available");
                try {
                    OrderMessage message = messages.take();
                    processPurchaseMessage(message);
                } catch (InterruptedException | OrderQuantityException e) {
                    log.error("Unable to process Order Message. Error:: {}.", e.getMessage());
                }
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    @Incoming("order-placed")
    @Merge
    @Blocking
    public void productPurchased(OrderMessage purchase) {
        log.error("=====> Order received for skuNumber :: " + purchase);
        messages.add(purchase);
    }


    @Transactional
    public void processPurchaseMessage(OrderMessage message) throws OrderQuantityException {
        itemService.processOrder(message.getSku(), message.getQuantity());
        salesService.processPurchase(message.getSku(), message.getQuantity());
    }
}
