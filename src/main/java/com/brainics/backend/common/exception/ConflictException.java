package com.brainics.backend.common.exception;

import org.springframework.http.HttpStatus;

/** For things like "username already exists". */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
