package com.challange.sky.api.domain.dto.inbound;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(max = 120, message = "Name must not exceed 120 characters")
        String name,

        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password
) {}
