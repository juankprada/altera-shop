package net.juankprada.exception;

public class OrderQuantityException extends Exception {
    private static final long serialVersionUID = 1L;

    public OrderQuantityException(String message) {
        super(message);
    }
}
