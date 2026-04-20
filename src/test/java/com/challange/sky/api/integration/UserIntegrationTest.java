package com.challange.sky.api.integration;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectResponse;
import com.challange.sky.api.domain.dto.outbound.UserResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate authenticatedClient() {
        return restTemplate.withBasicAuth("sky-admin", "sky-password");
    }

    @Test
    @Order(1)
    @DisplayName("Full user lifecycle: create -> get -> update -> delete")
    void userLifecycle() {
        var client = authenticatedClient();

        // Create user
        var createRequest = new CreateUserRequest("integration@test.com", "password123", "Integration User");
        var createResponse = client.postForEntity("/api/v1/users", createRequest, UserResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getHeaders().getLocation()).isNotNull();
        assertThat(createResponse.getBody()).isNotNull();

        Long userId = createResponse.getBody().id();
        assertThat(userId).isNotNull();
        assertThat(createResponse.getBody().email()).isEqualTo("integration@test.com");

        // Get user
        var getResponse = client.getForEntity("/api/v1/users/" + userId, UserResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name()).isEqualTo("Integration User");

        // Update user
        var updateRequest = new UpdateUserRequest("Updated User", null);
        var updateResponse = client.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                UserResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().name()).isEqualTo("Updated User");

        // Delete user
        var deleteResponse = client.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deleted
        var notFoundResponse = client.getForEntity("/api/v1/users/" + userId, String.class);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(2)
    @DisplayName("User-Project association: create project -> link -> list -> unlink")
    void userProjectAssociation() {
        var client = authenticatedClient();

        // Create user
        var userRequest = new CreateUserRequest("project-user@test.com", "password123", "Project User");
        var userResponse = client.postForEntity("/api/v1/users", userRequest, UserResponse.class);
        Long userId = userResponse.getBody().id();

        // Create project
        var projectRequest = new CreateProjectRequest("INT-PRJ-1", "Integration Project", "Test project");
        var projectResponse = client.postForEntity("/api/v1/projects", projectRequest, ProjectResponse.class);
        assertThat(projectResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Link project to user
        var linkResponse = client.postForEntity(
                "/api/v1/users/" + userId + "/projects/INT-PRJ-1",
                null,
                UserResponse.class
        );
        assertThat(linkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // List user projects
        var listResponse = client.exchange(
                "/api/v1/users/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectResponse>>() {}
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSize(1);
        assertThat(listResponse.getBody().getFirst().id()).isEqualTo("INT-PRJ-1");

        // Unlink project
        var unlinkResponse = client.exchange(
                "/api/v1/users/" + userId + "/projects/INT-PRJ-1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(unlinkResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify unlinked
        var emptyListResponse = client.exchange(
                "/api/v1/users/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectResponse>>() {}
        );
        assertThat(emptyListResponse.getBody()).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Should return 401 for unauthenticated request")
    void unauthenticated_returns401() {
        var response = restTemplate.getForEntity("/api/v1/users/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(4)
    @DisplayName("Should return 409 for duplicate email")
    void duplicateEmail_returns409() {
        var client = authenticatedClient();

        var request = new CreateUserRequest("duplicate@test.com", "password123", "First");
        client.postForEntity("/api/v1/users", request, UserResponse.class);

        var duplicateResponse = client.postForEntity("/api/v1/users", request, String.class);
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
