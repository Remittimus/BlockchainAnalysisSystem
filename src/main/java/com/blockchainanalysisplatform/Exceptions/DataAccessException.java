package com.blockchainanalysisplatform.Exceptions;

public class DataAccessException extends RuntimeException{
    // Конструктор с сообщением об ошибке
    public DataAccessException(String message) {
        super(message);
    }

    // Конструктор с сообщением об ошибке и исходным исключением
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор с исходным исключением
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
