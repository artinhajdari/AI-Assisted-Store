package com.demo.Store.service.impl;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.ai.SearchSummary;
import com.demo.Store.model.dto.mapper.ProductMapper;
import com.demo.Store.repository.ProductRepository;
import com.demo.Store.service.MessageSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatGenServiceImpl chatGenService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;

    private static final String MESSAGE = "message";

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void addOrUpdateReturnsInvalidWhenProductIdDoesNotExist() {
        product.setId(2L);
        when(productRepository.existsById(product.getId())).thenReturn(false);
        when(messageSourceService.getMessage("admin.products.ProductDoesNotExist")).thenReturn(MESSAGE);

        OperationResultDto result = service.addOrUpdate(product);

        assertFalse(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        verify(productRepository, never()).save(any());
        verify(vectorStore, never()).add(anyList());
    }

    @Test
    void addOrUpdateReturnsInvalidWhenDuplicateExists() {
        when(productRepository.existsDuplicate(product)).thenReturn(true);
        when(messageSourceService.getMessage("admin.products.ProductAlreadyExists")).thenReturn(MESSAGE);

        OperationResultDto result = service.addOrUpdate(product);

        assertFalse(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        verify(productRepository, never()).save(any());
        verify(vectorStore, never()).add(anyList());
    }

    @Test
    void addOrUpdateSavesAndIndexesProductWhenValid() {
        Product product2 = new Product();
        product2.setId(3L);
        when(productRepository.save(product)).thenReturn(product2);
        when(messageSourceService.getMessage("admin.products.ProductSaved")).thenReturn(MESSAGE);

        OperationResultDto result = service.addOrUpdate(product);

        assertTrue(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        ArgumentCaptor<List<Document>> docsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(docsCaptor.capture());
        Document indexedDocument = docsCaptor.getValue().getFirst();
        assertEquals(product2.getId(), indexedDocument.getMetadata().get("productId"));
    }

    @Test
    void getAllUsesRepositoryFindAllWhenPagingArgsAreNull() {
        List<Product> products = List.of(new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = service.getAll(null, null);

        assertSame(products, result);
        verify(productRepository).findAll();
    }

    @Test
    void getAllUsesPagedFindAllWhenPagingArgsExist() {
        List<Product> products = List.of(new Product());
        int page = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(page, pageSize);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));

        List<Product> result = service.getAll(page, pageSize);

        assertEquals(1, result.size());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void semanticSearchUsesVectorStoreAndChatModelFiltering() throws Exception {
        String prompt = "phone";
        Long productId = 5L;
        product.setId(productId);
        List<Product> daoReturnedProducts = List.of(product);
        ProductDto aiParseableProductDto = new ProductDto();
        aiParseableProductDto.setId(productId);
        List<ProductDto> aiParseableProducts = List.of(aiParseableProductDto);
        Document doc = new Document("1", "content", Map.of("productId", 5));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));
        when(productRepository.findAllById(List.of(5L))).thenReturn(daoReturnedProducts);
        when(productMapper.toAIParseableDtos(daoReturnedProducts)).thenReturn(aiParseableProducts);
        when(chatGenService.searchElements(prompt, aiParseableProducts)).thenReturn(SearchSummary.of(aiParseableProducts));

        List<Product> result = service.semanticSearch(prompt, 0);

        assertEquals(1, result.size());
        assertEquals(productId, result.getFirst().getId());
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
        verify(productRepository).findAllById(List.of(productId));
        verify(chatGenService).searchElements(prompt, aiParseableProducts);
    }

    @Test
    void deleteByIdRemovesEntityWhenExists() {
        Long productId = 4L;
        when(productRepository.existsById(productId)).thenReturn(true);
        when(messageSourceService.getMessage("admin.products.ProductDeleted")).thenReturn(MESSAGE);

        OperationResultDto result = service.deleteById(productId);

        assertTrue(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        verify(productRepository).deleteById(productId);
        verify(vectorStore).delete(anyList());
    }

    @Test
    void getByIdReadsRepository() {
        product.setId(8L);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Product result = service.getById(product.getId());
        assertSame(product, result);
        verify(productRepository).findById(product.getId());
    }
}
