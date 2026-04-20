package com.challange.sky.api.controllers;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
import com.challange.sky.api.services.ProjectService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private ProjectService projectService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("POST /api/v1/projects - should return 201")
    @WithMockUser
    void createProject_returns201() throws Exception {
        var request = new CreateProjectRequest("PRJ-1", "Project Alpha", "A great project");
        var response = new ProjectResponse("PRJ-1", "Project Alpha", "A great project", now, now);

        when(projectService.createProject(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value("PRJ-1"))
                .andExpect(jsonPath("$.name").value("Project Alpha"));
    }

    @Test
    @DisplayName("POST /api/v1/projects - should return 400 for invalid body")
    @WithMockUser
    void createProject_invalidBody_returns400() throws Exception {
        var request = new CreateProjectRequest("", "", null);

        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/projects/{id} - should return 200")
    @WithMockUser
    void getProjectById_returns200() throws Exception {
        var response = new ProjectResponse("PRJ-1", "Alpha", "Desc", now, now);
        when(projectService.getProjectById("PRJ-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/PRJ-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alpha"));
    }

    @Test
    @DisplayName("GET /api/v1/projects/{id} - should return 404 when not found")
    @WithMockUser
    void getProjectById_notFound_returns404() throws Exception {
        when(projectService.getProjectById("UNKNOWN")).thenThrow(new ResourceNotFoundException("Project", "UNKNOWN"));

        mockMvc.perform(get("/api/v1/projects/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/projects - should return 200 with list")
    @WithMockUser
    void getAllProjects_returns200() throws Exception {
        var r1 = new ProjectResponse("PRJ-1", "Alpha", null, now, now);
        var r2 = new ProjectResponse("PRJ-2", "Beta", null, now, now);
        when(projectService.getAllProjects()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
