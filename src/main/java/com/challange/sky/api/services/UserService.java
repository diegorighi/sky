package com.challange.sky.api.services;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.dto.outbound.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse addProjectToUser(Long userId, String projectId);

    void removeProjectFromUser(Long userId, String projectId);

    List<ProjectResponse> getUserProjects(Long userId);
}
