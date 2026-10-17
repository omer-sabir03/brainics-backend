package com.brainics.backend.common.exception;

import org.springframework.http.HttpStatus;

/** Base class for errors we throw on purpose. The handler turns them into clean JSON. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
