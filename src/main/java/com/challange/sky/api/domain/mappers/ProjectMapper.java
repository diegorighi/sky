package com.challange.sky.api.domain.mappers;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.entities.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(CreateProjectRequest request) {
        return new Project(request.id(), request.name(), request.description());
    }

    public ProjectResponse toResponse(Project project) {
        return ProjectResponse.from(project);
    }
}
