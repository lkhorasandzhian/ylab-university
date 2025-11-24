package ru.ylab.levon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.dto.UserResponseDto;
import ru.ylab.levon.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateDto dto);

    UserResponseDto toDto(User user);
}
