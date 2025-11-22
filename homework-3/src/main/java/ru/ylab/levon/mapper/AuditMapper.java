package ru.ylab.levon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.AuditRecordDto;
import ru.ylab.levon.model.AuditRecord;

import java.util.List;

@Mapper
public interface AuditMapper {

    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    AuditRecordDto toDto(AuditRecord record);

    List<AuditRecordDto> toDtoList(List<AuditRecord> records);
}
