package com.challange.sky.api.services;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectProjection;

import java.util.List;

public interface ProjectService {

    ProjectProjection createProject(CreateProjectRequest request);

    ProjectProjection getProjectById(String id);

    List<ProjectProjection> getAllProjects();
}
