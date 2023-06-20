package net.juankprada.orders.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.HashMap;
import java.util.Map;

@RegisterForReflection

public class PurchaseResultDto {
    
    public double total;

    public Map<String, String> result = new HashMap<>();


}
