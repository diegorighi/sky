package com.challange.sky.api.domain.mappers;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_shouldMapAllFields() {
        var request = new CreateUserRequest("test@test.com", "raw_password", "Test User");

        User entity = mapper.toEntity(request, "bcrypt_encoded");

        assertThat(entity.getEmail()).isEqualTo("test@test.com");
        assertThat(entity.getPassword()).isEqualTo("bcrypt_encoded"); // already encoded
        assertThat(entity.getName()).isEqualTo("Test User");
    }

    @Test
    void toResponse_shouldNotExposePassword() {
        var user = new User("test@test.com", "secret_hash", "Test");
        var response = mapper.toResponse(user);

        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("Test");
        // password should not be in response
    }

    @Test
    void updateEntity_shouldOnlyUpdateNonNullFields() {
        var user = new User("test@test.com", "old_hash", "Old Name");

        // update only name
        mapper.updateEntity(user, new UpdateUserRequest("New Name", null), null);
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getPassword()).isEqualTo("old_hash"); // unchanged

        // update only password
        mapper.updateEntity(user, new UpdateUserRequest(null, "ignored"), "new_hash");
        assertThat(user.getName()).isEqualTo("New Name"); // unchanged
        assertThat(user.getPassword()).isEqualTo("new_hash");
    }

    @Test
    void updateEntity_withBothFields() {
        var user = new User("test@test.com", "old_hash", "Old");

        mapper.updateEntity(user, new UpdateUserRequest("Updated", "newpw"), "encoded_pw");

        assertThat(user.getName()).isEqualTo("Updated");
        assertThat(user.getPassword()).isEqualTo("encoded_pw");
    }
}
