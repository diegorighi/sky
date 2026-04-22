package com.challange.sky.api.repositories;

import com.challange.sky.api.domain.dto.outbound.ProjectProjection;
import com.challange.sky.api.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("""
            SELECT p.id as id,
                   p.name as name,
                   p.description as description,
                   p.createdAt as createdAt,
                   p.updatedAt as updatedAt
            FROM Project p
            WHERE p.id = :id
            """)
    Optional<ProjectProjection> findProjectedById(String id);

    @Query("""
            SELECT p.id as id,
                   p.name as name,
                   p.description as description,
                   p.createdAt as createdAt,
                   p.updatedAt as updatedAt
            FROM Project p
            """)
    List<ProjectProjection> findAllProjected();

    @Query("""
            SELECT p.id as id,
                   p.name as name,
                   p.description as description,
                   p.createdAt as createdAt,
                   p.updatedAt as updatedAt
            FROM User u
            JOIN u.projects p
            WHERE u.id = :userId
            """)
    List<ProjectProjection> findProjectedByUserId(Long userId);
}
