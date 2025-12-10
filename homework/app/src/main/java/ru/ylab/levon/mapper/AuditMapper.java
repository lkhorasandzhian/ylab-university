package ru.ylab.levon.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.AuditRecordDto;
import ru.ylab.levon.model.AuditRecord;

/**
 * MapStruct-маппер для преобразования сущностей {@link AuditRecord}
 * в их DTO-представление {@link AuditRecordDto}.
 */
@Mapper
public interface AuditMapper {
    /**
     * Экземпляр маппера, генерируемый MapStruct.
     */
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    /**
     * Конвертирует запись аудита в DTO.
     *
     * @param record сущность аудита
     * @return DTO-объект
     */
    AuditRecordDto toDto(AuditRecord record);

    /**
     * Конвертирует список записей аудита в список DTO.
     *
     * @param records список сущностей
     * @return список DTO
     */
    List<AuditRecordDto> toDtoList(List<AuditRecord> records);
}
