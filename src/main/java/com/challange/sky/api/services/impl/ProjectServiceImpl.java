package com.challange.sky.api.services.impl;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.entities.Project;
import com.challange.sky.api.domain.exceptions.DuplicateResourceException;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
import com.challange.sky.api.domain.mappers.ProjectMapper;
import com.challange.sky.api.repositories.ProjectRepository;
import com.challange.sky.api.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        if (projectRepository.existsById(request.id())) {
            throw new DuplicateResourceException("Project", request.id());
        }

        Project project = projectMapper.toEntity(request);
        Project saved = projectRepository.save(project);

        log.info("Project created with id: {}", saved.getId());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(String id) {
        return projectRepository.findById(id)
                .map(projectMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .toList();
    }
}
