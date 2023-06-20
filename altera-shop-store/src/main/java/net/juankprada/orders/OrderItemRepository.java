package net.juankprada.orders;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.juankprada.orders.model.OrderItem;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepository<OrderItem> {
}
