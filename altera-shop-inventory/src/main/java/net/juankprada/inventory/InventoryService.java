package net.juankprada.inventory;

import io.fabric8.kubernetes.client.ResourceNotFoundException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.juankprada.exception.OrderQuantityException;
import net.juankprada.inventory.model.Category;
import net.juankprada.inventory.model.Item;
import net.juankprada.sales.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class InventoryService {

    private Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Inject
    private InventoryRepository itemRepository;

    @Inject
    private SaleRepository saleRepository;

    public List<Item> getCatalogueItems() {
        Sort sort = Sort.ascending("name");

        return itemRepository.listAll(sort);
    }

    public Item getItem(String skuNumber) throws ResourceNotFoundException {
        return itemRepository.findBySku(skuNumber).orElseThrow(() -> new ResourceNotFoundException(
                String.format("Item identified by SKU:: %s was not found.", skuNumber)));

    }


    public List<Item> getByCategory(Category category) {
        return itemRepository.findByCategory(category.getValue());
    }


    @Transactional
    public Long addItem(Item item) {
        item.setCreatedOn(LocalDate.now());
        itemRepository.persist(item);
        return item.getId();
    }

    @Transactional
    public void updateItem(Item item) throws ResourceNotFoundException {
        Item storedItem = getItem(item.getSku());

        boolean priceDifference = storedItem.getPrice() != item.getPrice();

        storedItem.setName(item.getName());
        storedItem.setDescription(item.getDescription());
        storedItem.setPrice(item.getPrice());
        storedItem.setInventory(item.getInventory());
        storedItem.setUpdatedOn(LocalDate.now());

        if (priceDifference) {
            log.error("===> Price is different compared to database's");
            // TODO: Product Price Update Event
        }

        itemRepository.persist(storedItem);
    }


    public void processOrder(String sku, int quantity) throws OrderQuantityException {
        log.info("====> Processing Order Event for {}", sku);

        Item inventoryItem = getItem(sku);

        if (inventoryItem.getInventory() >= quantity) {
            inventoryItem.setInventory(inventoryItem.getInventory() - quantity);
            itemRepository.persist(inventoryItem);
        } else {
            throw new OrderQuantityException(String.format("Order quantity is greater than the available inventory for SKU :: %s ", sku));
        }
    }


    @Transactional
    public void deleteItem(String skuNumber) throws ResourceNotFoundException {
        Item storedItem = getItem(skuNumber);

        itemRepository.delete(storedItem);

        itemRepository.flush();
    }


}
