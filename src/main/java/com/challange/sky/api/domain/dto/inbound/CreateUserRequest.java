package com.challange.sky.api.domain.dto.inbound;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 200, message = "Email must not exceed 200 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password,

        @Size(max = 120, message = "Name must not exceed 120 characters")
        String name
) {}
