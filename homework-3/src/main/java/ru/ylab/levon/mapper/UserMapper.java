package ru.ylab.levon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.dto.UserResponseDto;
import ru.ylab.levon.model.User;

/**
 * MapStruct-маппер для преобразования сущности {@link User}
 * в DTO и создания новых пользователей на основе входящих данных.
 */
@Mapper
public interface UserMapper {

    /**
     * Экземпляр маппера, генерируемый MapStruct.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Создаёт новую сущность пользователя из DTO регистрации.
     * <p>
     * Поля {@code id} и {@code role} игнорируются — они устанавливаются на уровне бизнес-логики.
     *
     * @param dto DTO с данными для создания пользователя
     * @return сущность {@link User}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateDto dto);

    /**
     * Преобразует сущность пользователя в DTO для ответа клиенту.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    UserResponseDto toDto(User user);
}
