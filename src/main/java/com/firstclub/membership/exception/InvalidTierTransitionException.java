package com.firstclub.membership.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTierTransitionException extends RuntimeException {

    public InvalidTierTransitionException(String message) {
        super(message);
    }
}
