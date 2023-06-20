package net.juankprada.inventory.model;

public enum Category {
    ICECREAM("Icecream"),
    SHAVED_ICE("Shaved Ice"),
    SNACK_BAR("Snack Bar"),
    BEVERAGE("Beverage");

    private String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}