package com.challange.sky.api.domain.dto.outbound;

import java.time.LocalDateTime;

public interface UserProjection {

    Long getId();

    String getEmail();

    String getName();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
