package net.juankprada.orders.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.juankprada.orders.validation.IEnumValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    @IEnumValidator(
            enumClazz = OrderStatus.class,
            message = "Invalid Status provided"
    )
    private String status;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDate createdOn;

    @Column(name = "UPDATED_ON", nullable = true, length = 19)
    private LocalDate updatedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return new EqualsBuilder().append(status, order.status).append(createdOn, order.createdOn).append(updatedOn, order.updatedOn).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(status).append(createdOn).append(updatedOn).toHashCode();
    }
}
