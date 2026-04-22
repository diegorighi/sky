package com.challange.sky.api.integration;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
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
import java.util.Map;

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
        var createResponse = client.postForEntity("/api/v1/users", createRequest, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getHeaders().getLocation()).isNotNull();
        assertThat(createResponse.getBody()).isNotNull();

        var userId = ((Number) createResponse.getBody().get("id")).longValue();
        assertThat(createResponse.getBody().get("email")).isEqualTo("integration@test.com");

        // Get user
        var getResponse = client.getForEntity("/api/v1/users/" + userId, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().get("name")).isEqualTo("Integration User");

        // Update user
        var updateRequest = new UpdateUserRequest("Updated User", null);
        var updateResponse = client.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Map.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().get("name")).isEqualTo("Updated User");

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
        var userResponse = client.postForEntity("/api/v1/users", userRequest, Map.class);
        var userId = ((Number) userResponse.getBody().get("id")).longValue();

        // Create project
        var projectRequest = new CreateProjectRequest("INT-PRJ-1", "Integration Project", "Test project");
        var projectResponse = client.postForEntity("/api/v1/projects", projectRequest, Map.class);
        assertThat(projectResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Link project to user (now returns 204)
        var linkResponse = client.postForEntity(
                "/api/v1/users/" + userId + "/projects/INT-PRJ-1",
                null,
                Void.class
        );
        assertThat(linkResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // List user projects
        var listResponse = client.exchange(
                "/api/v1/users/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSize(1);
        assertThat(listResponse.getBody().getFirst().get("id")).isEqualTo("INT-PRJ-1");

        // Unlink project
        var unlinkResponse = client.exchange(
                "/api/v1/users/" + userId + "/projects/INT-PRJ-1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(unlinkResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify empty
        var emptyList = client.exchange(
                "/api/v1/users/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(emptyList.getBody()).isEmpty();
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
        client.postForEntity("/api/v1/users", request, Map.class);

        var duplicateResponse = client.postForEntity("/api/v1/users", request, String.class);
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
