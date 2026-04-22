package com.challange.sky.api.domain.dto.outbound;

import java.time.LocalDateTime;

public interface ProjectProjection {

    String getId();

    String getName();

    String getDescription();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
