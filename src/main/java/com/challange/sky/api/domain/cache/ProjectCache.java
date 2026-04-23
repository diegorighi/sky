package com.challange.sky.api.domain.cache;

import com.challange.sky.api.domain.entities.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory cache for Project entities using Enum Singleton pattern.
 * The JVM guarantees enum instantiation is atomic and happens once,
 * avoiding the need for synchronized blocks, volatile fields, or double-checked locking.
 */
public enum ProjectCache {

    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(ProjectCache.class);

    private final ConcurrentMap<String, Project> cache = new ConcurrentHashMap<>();

    public Optional<Project> get(String projectId) {
        return Optional.ofNullable(cache.get(projectId));
    }

    public void put(Project project) {
        cache.put(project.getId(), project);
        log.debug("Project cached: {}", project.getId());
    }

    public void evict(String projectId) {
        cache.remove(projectId);
        log.debug("Project evicted from cache: {}", projectId);
    }

    public void clear() {
        cache.clear();
        log.debug("Project cache cleared");
    }

    public int size() {
        return cache.size();
    }
}
