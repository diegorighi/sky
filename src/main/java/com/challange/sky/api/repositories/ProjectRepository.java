package com.challange.sky.api.repositories;

import com.challange.sky.api.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
