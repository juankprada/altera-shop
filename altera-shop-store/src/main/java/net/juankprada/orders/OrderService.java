package net.juankprada.orders;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.juankprada.orders.dto.ItemDto;
import net.juankprada.orders.dto.OrderDto;
import net.juankprada.orders.dto.OrderItemDto;
import net.juankprada.orders.dto.PurchaseResultDto;
import net.juankprada.orders.event.OrderPlacedEvent;
import net.juankprada.orders.model.Order;
import net.juankprada.orders.model.OrderItem;
import net.juankprada.orders.model.OrderMessage;
import net.juankprada.orders.model.OrderStatus;
import net.juankprada.orders.proxy.InventoryProxy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderService {

    private Logger log = LoggerFactory.getLogger(OrderService.class);

    @Inject
    @RestClient
    InventoryProxy inventoryProxy;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private OrderItemRepository orderItemRepository;

    @Inject
    private OrderPlacedEvent orderPlacedEvent;

    @Transactional
    public PurchaseResultDto placeOrder(OrderDto orderDto) throws Exception {

        PurchaseResultDto result = new PurchaseResultDto();

        // Make sure we get the right quantities purchased for each sku
        Map<String, Integer> orders = orderDto.items.stream().collect(Collectors.toMap(OrderItemDto::getSku, OrderItemDto::getQuantity, Integer::sum));

        HashMap<String, ItemDto> cachedItems = new HashMap<>();

        // Set subtotal values for every SKU
        Map<String, Double> subTotalMap = orders.entrySet().stream().collect(
                HashMap::new,
                (map, e) -> {
                    String sku = e.getKey();

                    // Avoid requesting the same Item multiple times and chache them.
                    ItemDto inventoryItem = cachedItems.get(sku);
                    if (inventoryItem == null) {
                        inventoryItem = inventoryProxy.getInventoryItem(sku);
                        cachedItems.put(sku, inventoryItem);
                    }

                    int quantityPurchased = orders.get(sku);

                    // Do we have enough items to sell?
                    if (inventoryItem != null && inventoryItem.getInventory() >= quantityPurchased) {
                        map.put(sku, quantityPurchased * inventoryItem.getPrice());
                        result.result.put(inventoryItem.getName(), "ENJOY!");
                    } else {
                        map.put(sku, null);
                        assert inventoryItem != null;
                        result.result.put(inventoryItem.getName(), "SO SORRY!");
                    }
                },
                HashMap::putAll);

        // Now create an order only if we actually could sell something
        //TODO: This could be done async
        boolean couldSell = subTotalMap.entrySet().stream().anyMatch(e -> e.getValue() != null);
        if (couldSell) {
            // Place Order
            Order order = new Order();
            order.setCreatedOn(LocalDate.now());
            order.setStatus(OrderStatus.PROCESSING.name());

            orderRepository.persist(order);

            orders.forEach((sku, quantity) -> {

                Double subtotal = subTotalMap.get(sku);
                if (subtotal != null) {

                    OrderItem orderItem = new OrderItem(order, sku, quantity, subtotal);

                    orderPlacedEvent.add(new OrderMessage(sku, quantity));

                    orderItemRepository.persist(orderItem);
                }
            });
        }

        // Set the total price for the sale
        result.total = subTotalMap.values().stream().filter(Objects::nonNull).mapToDouble(aDouble -> aDouble).sum();

        return result;

    }
}
