package com.java.hotel.dao.exception;
public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}