package com.my.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
      super("Пользователь не аутентифицирован.");
    }
}
