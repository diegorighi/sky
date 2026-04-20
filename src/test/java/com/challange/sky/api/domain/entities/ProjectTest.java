package com.challange.sky.api.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void createProject() {
        Project p = new Project("JIRA-123", "My Project", "Some description");

        assertEquals("JIRA-123", p.getId());
        assertEquals("My Project", p.getName());
        assertEquals("Some description", p.getDescription());
    }

    @Test
    void nullDescription() {
        Project p = new Project("GH-456", "GitHub Project", null);

        assertNull(p.getDescription());
        assertEquals("GH-456", p.getId());
    }

    @Test
    void equalsContract() {
        Project p1 = new Project("ABC", "P1", null);
        Project p2 = new Project("ABC", "P2", "different desc");
        Project p3 = new Project("XYZ", "P1", null);

        // same id = equal
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());

        // different id = not equal
        assertNotEquals(p1, p3);
    }

    @Test
    void settersWork() {
        Project p = new Project("ID-1", "Original", "Desc");

        p.setName("Updated Name");
        p.setDescription("Updated Desc");

        assertEquals("Updated Name", p.getName());
        assertEquals("Updated Desc", p.getDescription());
    }
}
