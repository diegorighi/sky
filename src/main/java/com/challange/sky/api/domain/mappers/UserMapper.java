package com.challange.sky.api.domain.mappers;

import com.challange.sky.api.domain.dto.inbound.CreateUserRequest;
import com.challange.sky.api.domain.dto.inbound.UpdateUserRequest;
import com.challange.sky.api.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request, String encodedPassword) {
        return new User(request.email(), encodedPassword, request.name());
    }

    public void updateEntity(User user, UpdateUserRequest request, String encodedPassword) {
        if (request.name() != null) {
            user.setName(request.name());
        }
        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
        }
    }
}
