package org.habitApp.services;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDto sayHello() throws SQLException {
        return userMapper.userToUserDto(userRepository.getUserByEmail("root"));
//        return new UserDto(UUID.randomUUID(), "user@example.com", "password", "Regular User", false);
    }
}
