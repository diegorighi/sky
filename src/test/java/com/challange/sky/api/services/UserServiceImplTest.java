package com.challange.sky.api.services;

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
import com.challange.sky.api.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User("john@example.com", "encoded-password", "John Doe");
        userResponse = new UserResponse(1L, "john@example.com", "John Doe", LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("should create user successfully")
        void shouldCreateUserSuccessfully() {
            var request = new CreateUserRequest("john@example.com", "password123", "John Doe");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
            when(userMapper.toEntity(request, "encoded-password")).thenReturn(user);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.createUser(request);

            assertThat(result).isEqualTo(userResponse);
            verify(passwordEncoder).encode(request.password());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when email exists")
        void shouldThrowWhenEmailExists() {
            var request = new CreateUserRequest("john@example.com", "password123", "John Doe");
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.getUserById(1L);

            assertThat(result).isEqualTo(userResponse);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("should update user with name and password")
        void shouldUpdateUserWithNameAndPassword() {
            var request = new UpdateUserRequest("Jane Doe", "newpassword");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newpassword")).thenReturn("encoded-new");
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.updateUser(1L, request);

            assertThat(result).isNotNull();
            verify(userMapper).updateEntity(user, request, "encoded-new");
        }

        @Test
        @DisplayName("should update user with only name")
        void shouldUpdateUserWithOnlyName() {
            var request = new UpdateUserRequest("Jane Doe", null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            userService.updateUser(1L, request);

            verify(passwordEncoder, never()).encode(anyString());
            verify(userMapper).updateEntity(user, request, null);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void shouldThrowWhenNotFound() {
            var request = new UpdateUserRequest("Jane", null);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            when(userRepository.existsById(1L)).thenReturn(true);

            userService.deleteUser(1L);

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void shouldThrowWhenNotFound() {
            when(userRepository.existsById(1L)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("addProjectToUser")
    class AddProjectToUser {

        @Test
        @DisplayName("should link project to user")
        void shouldLinkProjectToUser() {
            var project = new Project("PRJ-1", "Project Alpha", "Description");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(projectRepository.findById("PRJ-1")).thenReturn(Optional.of(project));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.addProjectToUser(1L, "PRJ-1");

            assertThat(result).isNotNull();
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.addProjectToUser(1L, "PRJ-1"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw when project not found")
        void shouldThrowWhenProjectNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(projectRepository.findById("PRJ-1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.addProjectToUser(1L, "PRJ-1"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getUserProjects")
    class GetUserProjects {

        @Test
        @DisplayName("should return user projects")
        void shouldReturnUserProjects() {
            var project = new Project("PRJ-1", "Project Alpha", "Desc");
            user.addProject(project);
            var projectResponse = new ProjectResponse("PRJ-1", "Project Alpha", "Desc", LocalDateTime.now(), LocalDateTime.now());

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            List<ProjectResponse> result = userService.getUserProjects(1L);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo("PRJ-1");
        }
    }
}
