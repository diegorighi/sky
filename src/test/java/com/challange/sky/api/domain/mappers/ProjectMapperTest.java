package com.challange.sky.api.domain.mappers;

import com.challange.sky.api.domain.dto.inbound.CreateProjectRequest;
import com.challange.sky.api.domain.entities.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    private ProjectMapper mapper = new ProjectMapper();

    @Test
    void testToEntity() {
        var req = new CreateProjectRequest("PROJ-1", "Project One", "A desc");
        Project entity = mapper.toEntity(req);

        assertEquals("PROJ-1", entity.getId());
        assertEquals("Project One", entity.getName());
        assertEquals("A desc", entity.getDescription());
    }

    @Test
    void testToEntityWithNullDescription() {
        var req = new CreateProjectRequest("PROJ-2", "No Desc", null);
        Project entity = mapper.toEntity(req);

        assertNull(entity.getDescription());
    }
}
