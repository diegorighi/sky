package com.challange.sky.api.domain.dto.outbound;

import com.challange.sky.api.domain.entities.Project;
import java.time.LocalDateTime;

public record ProjectResponse(
        String id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
