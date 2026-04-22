package com.challange.sky.api.controllers;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.dto.outbound.ProjectProjection;
import com.challange.sky.api.domain.dto.outbound.UserProjection;
import com.challange.sky.api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<UserProjection> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserProjection created = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserProjection> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user information")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserProjection> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/projects/{projectId}")
    @Operation(summary = "Link a project to a user")
    @ApiResponse(responseCode = "204", description = "Project linked successfully")
    public ResponseEntity<Void> addProjectToUser(@PathVariable Long userId,
                                                 @PathVariable String projectId) {
        userService.addProjectToUser(userId, projectId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/projects/{projectId}")
    @Operation(summary = "Unlink a project from a user")
    @ApiResponse(responseCode = "204", description = "Project unlinked successfully")
    public ResponseEntity<Void> removeProjectFromUser(@PathVariable Long userId,
                                                      @PathVariable String projectId) {
        userService.removeProjectFromUser(userId, projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/projects")
    @Operation(summary = "List projects associated with a user")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    public ResponseEntity<List<ProjectProjection>> getUserProjects(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProjects(userId));
    }
}
