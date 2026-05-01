package com.demo.Store.model.dto.mapper;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.ProductDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Named("toStandardDto")
    ProductDto toDto(Product product);

    @IterableMapping(qualifiedByName = "toStandardDto")
    List<ProductDto> toDtos(List<Product> products);

    Product toProduct(ProductDto productDto);

    @Named("toAIParseableDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayedDescription", source = "displayedDescription")
    ProductDto toAIParseableDto(Product product);

    @IterableMapping(qualifiedByName = "toAIParseableDto")
    List<ProductDto> toAIParseableDtos(List<Product> product);

}
