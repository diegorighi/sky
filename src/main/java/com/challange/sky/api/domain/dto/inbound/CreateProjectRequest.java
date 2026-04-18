package com.challange.sky.api.domain.dto.inbound;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(

        @NotBlank(message = "Project ID is required")
        @Size(max = 200, message = "Project ID must not exceed 200 characters")
        String id,

        @NotBlank(message = "Project name is required")
        @Size(max = 120, message = "Project name must not exceed 120 characters")
        String name,

        String description
) {}
