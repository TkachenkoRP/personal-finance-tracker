package com.my.exception;

public class TransactionCategoryException extends RuntimeException {
    public TransactionCategoryException(String message) {
        super(message);
    }
}
