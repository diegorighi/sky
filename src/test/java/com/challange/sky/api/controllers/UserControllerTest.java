package com.challange.sky.api.controllers;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.dto.outbound.UserResponse;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
import com.challange.sky.api.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private UserService userService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("POST /api/v1/users - should return 201")
    @WithMockUser
    void createUser_returns201() throws Exception {
        var request = new CreateUserRequest("john@example.com", "password123", "John");
        var response = new UserResponse(1L, "john@example.com", "John", now, now);

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/users - should return 400 for invalid body")
    @WithMockUser
    void createUser_invalidBody_returns400() throws Exception {
        var request = new CreateUserRequest("", "", null);

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - should return 200")
    @WithMockUser
    void getUserById_returns200() throws Exception {
        var response = new UserResponse(1L, "john@example.com", "John", now, now);
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - should return 404 when not found")
    @WithMockUser
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - should return 200")
    @WithMockUser
    void updateUser_returns200() throws Exception {
        var request = new UpdateUserRequest("Jane", null);
        var response = new UserResponse(1L, "john@example.com", "Jane", now, now);

        when(userService.updateUser(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - should return 204")
    @WithMockUser
    void deleteUser_returns204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/users - should return 401 when unauthenticated")
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/projects/{projectId} - should return 200")
    @WithMockUser
    void addProjectToUser_returns200() throws Exception {
        var response = new UserResponse(1L, "john@example.com", "John", now, now);
        when(userService.addProjectToUser(1L, "PRJ-1")).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/1/projects/PRJ-1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/projects - should return 200")
    @WithMockUser
    void getUserProjects_returns200() throws Exception {
        var projectResponse = new ProjectResponse("PRJ-1", "Alpha", "Desc", now, now);
        when(userService.getUserProjects(1L)).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/api/v1/users/1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PRJ-1"));
    }
}
