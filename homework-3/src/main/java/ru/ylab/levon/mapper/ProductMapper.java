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

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product entity);

    ProductResponseDto toDto(Product product);

    List<ProductResponseDto> toDtoList(Collection<Product> products);
}
