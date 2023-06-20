package net.juankprada.sales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "SALES")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "SKU cannot be null or empty")
    @Column(name = "SKU_NUMBER", nullable = false, length = 16)
    private String sku;

    @NotNull(message = "Quantity cannot be null or empty")
    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @NotNull(message = "Value cannot be null or empty")
    @Column(name = "VALUE", nullable = false, precision = 10)
    private Double value;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
}
