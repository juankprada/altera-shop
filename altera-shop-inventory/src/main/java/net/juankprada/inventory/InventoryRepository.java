package net.juankprada.inventory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.juankprada.inventory.model.Item;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryRepository implements PanacheRepository<Item> {

    public Optional<Item> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }


    public List<Item> findByCategory(String category) {
        return find("category", category).list();
    }



}
