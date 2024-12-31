package com.example.cryptotrading.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public interface ErrorType extends Serializable {
    String getCode();
    String getDesc();
    HttpStatus getHttpStatusCode();
}