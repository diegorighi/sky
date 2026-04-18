package com.challange.sky.api.domain.dto.outbound;

import com.challange.sky.api.domain.entities.User;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
