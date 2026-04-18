package com.challange.sky.api.domain.dto.outbound;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {}
