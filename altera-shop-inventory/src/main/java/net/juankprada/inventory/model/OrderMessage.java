package net.juankprada.inventory.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderMessage {

    private String sku;
    private Integer quantity;

    public OrderMessage() {
    }

    public OrderMessage(String sku, Integer quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
