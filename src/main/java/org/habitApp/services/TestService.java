package org.habitApp.services;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestService {
    public UserDto sayHello() {
        return new UserDto(UUID.randomUUID(), "user@example.com", "password", "Regular User", false);
    }
}
