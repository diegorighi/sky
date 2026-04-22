package com.challange.sky.api.services;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectProjection;
import com.challange.sky.api.domain.dto.outbound.UserProjection;

import java.util.List;

public interface UserService {

    UserProjection createUser(CreateUserRequest request);

    UserProjection getUserById(Long id);

    UserProjection updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    void addProjectToUser(Long userId, String projectId);

    void removeProjectFromUser(Long userId, String projectId);

    List<ProjectProjection> getUserProjects(Long userId);
}
