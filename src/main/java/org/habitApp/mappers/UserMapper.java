package org.habitApp.mappers;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(UserEntity user);
    UserEntity userDtoToUser(UserDto userDto);
}


