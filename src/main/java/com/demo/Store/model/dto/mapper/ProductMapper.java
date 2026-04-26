package com.demo.Store.model.dto.mapper;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto toDto(Product product);

    List<ProductDto> toDtos(List<Product> products);

    Product toProduct(ProductDto productDto);

}
