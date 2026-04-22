package com.challange.sky.api.repositories;

import com.challange.sky.api.domain.dto.outbound.UserProjection;
import com.challange.sky.api.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u.id as id,
                   u.email as email,
                   u.name as name,
                   u.createdAt as createdAt,
                   u.updatedAt as updatedAt
            FROM User u
            WHERE u.id = :id
            """)
    Optional<UserProjection> findProjectedById(Long id);

    @Query("""
            SELECT u.id as id,
                   u.email as email,
                   u.name as name,
                   u.createdAt as createdAt,
                   u.updatedAt as updatedAt
            FROM User u
            """)
    List<UserProjection> findAllProjected();
}
