package com.my.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("У Вас нет прав доступа!");
    }
}
