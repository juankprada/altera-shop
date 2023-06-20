package net.juankprada.orders.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ORDER_ITEMS")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne()
    private Order order;

    @NotNull
    @Column(name = "SKU_NUMBER", nullable = false, length = 16)
    private String skuNumber;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 10)
    private Double price;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDate createdOn;

    public OrderItem() {
        this.createdOn = LocalDate.now();
    }

    public OrderItem(@NotNull Order order, @NotNull String skuNumber, @NotNull Integer quantity, @NotNull Double price) {
        this.order = order;
        this.skuNumber = skuNumber;
        this.quantity = quantity;
        this.price = price;
        this.createdOn = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getSkuNumber() {
        return skuNumber;
    }

    public void setSkuNumber(String skuNumber) {
        this.skuNumber = skuNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        return new EqualsBuilder().append(order, orderItem.order).append(skuNumber, orderItem.skuNumber).append(quantity, orderItem.quantity).append(price, orderItem.price).append(createdOn, orderItem.createdOn).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(order).append(skuNumber).append(quantity).append(price).append(createdOn).toHashCode();
    }
}
