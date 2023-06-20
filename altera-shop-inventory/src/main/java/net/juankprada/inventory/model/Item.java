package net.juankprada.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import net.juankprada.validation.IEnumValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ITEMS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "SKU_NUMBER")
        })
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "SKU cannot be null or empty")
    @Column(name = "SKU_NUMBER", unique = true, nullable = false, length = 16)
    private String sku;

    @NotEmpty(message = "Name cannot be null or empty")
    @Column(name = "ITEM_NAME", unique = true, nullable = false, length = 255)
    private String name;

    @NotEmpty(message = "Description cannot be null or empty")
    @Column(name = "DESCRIPTION", nullable = false, length = 500)
    private String description;

    @Column(name = "CATEGORY", nullable = false)
    @IEnumValidator(
            enumClazz = Category.class,
            message = "Invalid category provided"
    )
    private String category;

    @NotNull(message = "Price cannot be null or empty")
    @Column(name = "PRICE", nullable = false, precision = 10)
    private Double price;

    @NotNull(message = "Inventory cannot be null or empty")
    @Column(name = "INVENTORY", nullable = false)
    private Integer inventory;

    @Column(name = "CREATED_ON", nullable = false, length = 19)
    private LocalDate createdOn;

    @Column(name = "UPDATED_ON", nullable = true, length = 19)
    private LocalDate updatedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDate getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDate updatedOn) {
        this.updatedOn = updatedOn;
    }

    public void updateValues(Item item) {
        name = item.name;
        category = item.category;
        description = item.description;
        inventory = item.inventory;
        price = item.price;
    }

    public Item() {
    }

    public Item(String sku, String name, String description, String category, @NotNull(message = "Price cannot be null or empty") Double price, @NotNull(message = "Inventory cannot be null or empty") Integer inventory) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.inventory = inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return new EqualsBuilder().append(sku, item.sku).append(name, item.name).append(description, item.description).append(category, item.category).append(price, item.price).append(inventory, item.inventory).append(createdOn, item.createdOn).append(updatedOn, item.updatedOn).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(sku).append(name).append(description).append(category).append(price).append(inventory).append(createdOn).append(updatedOn).toHashCode();
    }
}
