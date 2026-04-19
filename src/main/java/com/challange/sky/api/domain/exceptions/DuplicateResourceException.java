package com.challange.sky.api.domain.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resource, Object identifier) {
        super("%s already exists with identifier: %s".formatted(resource, identifier), HttpStatus.CONFLICT);
    }
}
