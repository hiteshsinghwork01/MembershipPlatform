package com.firstclub.membership.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadySubscribedException extends RuntimeException {

    public AlreadySubscribedException(Long userId) {
        super("User " + userId + " already has an active membership");
    }
}
