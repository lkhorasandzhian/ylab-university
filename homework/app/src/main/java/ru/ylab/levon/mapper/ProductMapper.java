package ru.ylab.levon.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductResponseDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;

/**
 * MapStruct-маппер для преобразования сущности {@link Product}
 * в её DTO-представления и обратно.
 */
@Mapper
public interface ProductMapper {
    /**
     * Экземпляр маппера, генерируемый MapStruct.
     */
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    /**
     * Создаёт новую сущность продукта на основе DTO создания.
     * Поле {@code id} игнорируется и заполняется на уровне БД.
     *
     * @param dto данные для создания продукта
     * @return новая сущность {@link Product}
     */
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCreateDto dto);

    /**
     * Обновляет существующую сущность продукта значениями из DTO обновления.
     * <p>
     * Поля с {@code null} не перезаписывают существующие значения.
     *
     * @param dto    данные для обновления
     * @param entity сущность, которую нужно обновить
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product entity);

    /**
     * Конвертирует сущность продукта в DTO для ответа клиенту.
     *
     * @param product сущность
     * @return DTO продукта
     */
    ProductResponseDto toDto(Product product);

    /**
     * Конвертирует коллекцию сущностей продуктов в список DTO.
     *
     * @param products коллекция сущностей
     * @return список DTO
     */
    List<ProductResponseDto> toDtoList(Collection<Product> products);
}
