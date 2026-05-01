package com.demo.Store.controller;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.mapper.ProductMapper;
import com.demo.Store.service.ChatGenService;
import com.demo.Store.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ChatGenService chatGenService;

    @InjectMocks
    private StoreController controller;

    @Test
    void homeReturnsIndexView() {
        assertEquals("index", controller.home());
    }

    @Test
    void searchReturnsMappedProductsWhenQueryIsBlank() throws Exception {
        List<Product> products = List.of(new Product());
        List<ProductDto> mappedProducts = List.of(new ProductDto());
        when(productService.semanticSearch(anyString(), anyInt())).thenReturn(products);
        when(productMapper.toDtos(products)).thenReturn(mappedProducts);

        List<ProductDto> result = controller.search(anyString(), anyInt());

        assertSame(mappedProducts, result);
        verify(chatGenService, never()).searchElements(anyString(), anyList());
    }
}