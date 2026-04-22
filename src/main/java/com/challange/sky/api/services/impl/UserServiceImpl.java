package com.challange.sky.api.services.impl;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectProjection;
import com.challange.sky.api.domain.dto.outbound.UserProjection;
import com.challange.sky.api.domain.entities.Project;
import com.challange.sky.api.domain.entities.User;
import com.challange.sky.api.domain.exceptions.DuplicateResourceException;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserProjection createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userMapper.toEntity(request, encodedPassword);
        User saved = userRepository.save(user);

        log.info("User created with id: {}", saved.getId());
        return findProjectionOrThrow(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProjection getUserById(Long id) {
        return findProjectionOrThrow(id);
    }

    @Override
    @Transactional
    public UserProjection updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        String encodedPassword = request.password() != null
                ? passwordEncoder.encode(request.password())
                : null;

        userMapper.updateEntity(user, request, encodedPassword);
        userRepository.save(user);

        log.info("User updated with id: {}", id);
        return findProjectionOrThrow(id);
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
    public void addProjectToUser(Long userId, String projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        user.addProject(project);
        userRepository.save(user);

        log.info("Project {} linked to user {}", projectId, userId);
    }

    @Override
    @Transactional
    public void removeProjectFromUser(Long userId, String projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        user.removeProject(project);
        userRepository.save(user);

        log.info("Project {} unlinked from user {}", projectId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectProjection> getUserProjects(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return projectRepository.findProjectedByUserId(userId);
    }

    private UserProjection findProjectionOrThrow(Long id) {
        return userRepository.findProjectedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
