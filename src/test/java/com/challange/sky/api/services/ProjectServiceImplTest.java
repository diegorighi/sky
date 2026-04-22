package com.challange.sky.api.services;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectProjection;
import com.challange.sky.api.domain.entities.Project;
import com.challange.sky.api.domain.exceptions.DuplicateResourceException;
import com.challange.sky.api.domain.exceptions.ResourceNotFoundException;
import com.challange.sky.api.domain.mappers.ProjectMapper;
import com.challange.sky.api.repositories.ProjectRepository;
import com.challange.sky.api.services.impl.ProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Nested
    @DisplayName("createProject")
    class CreateProject {

        @Test
        @DisplayName("should create project successfully")
        void shouldCreateProjectSuccessfully() {
            var request = new CreateProjectRequest("PRJ-1", "Project Alpha", "A description");
            var project = new Project("PRJ-1", "Project Alpha", "A description");
            var projection = mock(ProjectProjection.class);
            when(projection.getId()).thenReturn("PRJ-1");
            when(projection.getName()).thenReturn("Project Alpha");

            when(projectRepository.existsById("PRJ-1")).thenReturn(false);
            when(projectMapper.toEntity(request)).thenReturn(project);
            when(projectRepository.save(project)).thenReturn(project);
            when(projectRepository.findProjectedById("PRJ-1")).thenReturn(Optional.of(projection));

            ProjectProjection result = projectService.createProject(request);

            assertThat(result.getId()).isEqualTo("PRJ-1");
            assertThat(result.getName()).isEqualTo("Project Alpha");
            verify(projectRepository).save(project);
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when project exists")
        void shouldThrowWhenProjectExists() {
            var request = new CreateProjectRequest("PRJ-1", "Project Alpha", null);
            when(projectRepository.existsById("PRJ-1")).thenReturn(true);

            assertThatThrownBy(() -> projectService.createProject(request))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(projectRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getProjectById")
    class GetProjectById {

        @Test
        @DisplayName("should return project projection when found")
        void shouldReturnProjectWhenFound() {
            var projection = mock(ProjectProjection.class);
            when(projection.getId()).thenReturn("PRJ-1");

            when(projectRepository.findProjectedById("PRJ-1")).thenReturn(Optional.of(projection));

            ProjectProjection result = projectService.getProjectById("PRJ-1");

            assertThat(result.getId()).isEqualTo("PRJ-1");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(projectRepository.findProjectedById("PRJ-99")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.getProjectById("PRJ-99"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllProjects")
    class GetAllProjects {

        @Test
        @DisplayName("should return all projects")
        void shouldReturnAllProjects() {
            var p1 = mock(ProjectProjection.class);
            var p2 = mock(ProjectProjection.class);

            when(projectRepository.findAllProjected()).thenReturn(List.of(p1, p2));

            List<ProjectProjection> result = projectService.getAllProjects();

            assertThat(result).hasSize(2);
        }
    }
}
