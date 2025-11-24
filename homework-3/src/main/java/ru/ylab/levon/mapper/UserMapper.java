package ru.ylab.levon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.dto.UserResponseDto;
import ru.ylab.levon.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserCreateDto dto);

    UserResponseDto toDto(User user);
}
