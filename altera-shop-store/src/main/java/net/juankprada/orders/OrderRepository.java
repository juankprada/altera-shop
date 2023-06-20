package net.juankprada.orders;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.juankprada.orders.model.Order;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
}
