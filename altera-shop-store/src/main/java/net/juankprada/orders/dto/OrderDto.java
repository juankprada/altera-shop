package net.juankprada.orders.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class OrderDto {
    public List<OrderItemDto> items;

}
