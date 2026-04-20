package com.challange.sky.api.domain.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithCorrectFields() {
        User user = new User("test@email.com", "hashed_pwd", "Test User");

        assertEquals("test@email.com", user.getEmail());
        assertEquals("hashed_pwd", user.getPassword());
        assertEquals("Test User", user.getName());
        assertNotNull(user.getProjects());
        assertTrue(user.getProjects().isEmpty());
    }

    @Test
    void shouldAddAndRemoveProject() {
        User user = new User("test@email.com", "pwd", "User");
        Project project = new Project("EXT-1", "External Project", "desc");

        user.addProject(project);

        assertThat(user.getProjects()).contains(project);
        assertThat(project.getUsers()).contains(user);

        user.removeProject(project);

        assertThat(user.getProjects()).doesNotContain(project);
        assertThat(project.getUsers()).doesNotContain(user);
    }

    @Test
    void testEqualsAndHashCode() {
        // two users without id should not be equal
        User u1 = new User("a@a.com", "pwd", "A");
        User u2 = new User("b@b.com", "pwd", "B");

        assertNotEquals(u1, u2);
        assertNotEquals(u1, null);
        assertNotEquals(u1, "not a user");
        assertEquals(u1, u1); // same reference
    }

    @Test
    void toStringShouldContainEmail() {
        User user = new User("diego@sky.com", "pwd", "Diego");
        String str = user.toString();

        assertTrue(str.contains("diego@sky.com"));
        assertTrue(str.contains("Diego"));
    }

    @Test
    void shouldUpdatePassword() {
        User user = new User("test@email.com", "old_password", "Test");

        user.setPassword("new_password");

        assertEquals("new_password", user.getPassword());
    }
}
