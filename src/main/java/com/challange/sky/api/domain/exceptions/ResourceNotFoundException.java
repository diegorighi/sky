package com.challange.sky.api.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super("%s not found with identifier: %s".formatted(resource, identifier), HttpStatus.NOT_FOUND);
    }
}
