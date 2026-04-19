package com.challange.sky.api.services;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse getProjectById(String id);

    List<ProjectResponse> getAllProjects();
}
