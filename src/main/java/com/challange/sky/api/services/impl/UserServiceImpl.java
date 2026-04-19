package com.challange.sky.api.services.impl;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.dto.outbound.UserResponse;
import com.challange.sky.api.domain.entities.Project;
import com.challange.sky.api.domain.entities.User;
import com.challange.sky.api.domain.exceptions.DuplicateResourceException;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
import com.challange.sky.api.domain.mappers.ProjectMapper;
import com.challange.sky.api.domain.mappers.UserMapper;
import com.challange.sky.api.repositories.ProjectRepository;
import com.challange.sky.api.repositories.UserRepository;
import com.challange.sky.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           UserMapper userMapper,
                           ProjectMapper projectMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userMapper.toEntity(request, encodedPassword);
        User saved = userRepository.save(user);

        log.info("User created with id: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        String encodedPassword = request.password() != null
                ? passwordEncoder.encode(request.password())
                : null;

        userMapper.updateEntity(user, request, encodedPassword);
        User updated = userRepository.save(user);

        log.info("User updated with id: {}", updated.getId());
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Override
    @Transactional
    public UserResponse addProjectToUser(Long userId, String projectId) {
        User user = findUserOrThrow(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        user.addProject(project);
        User updated = userRepository.save(user);

        log.info("Project {} linked to user {}", projectId, userId);
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void removeProjectFromUser(Long userId, String projectId) {
        User user = findUserOrThrow(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        user.removeProject(project);
        userRepository.save(user);

        log.info("Project {} unlinked from user {}", projectId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(Long userId) {
        User user = findUserOrThrow(userId);
        return user.getProjects().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
