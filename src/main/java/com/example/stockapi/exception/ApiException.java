package com.example.stockapi.exception;

import lombok.Getter;

public class ApiException extends Exception{

    @Getter
    private final String message;

    public ApiException(String message){
        super(message);
        this.message = message;
    }
}
