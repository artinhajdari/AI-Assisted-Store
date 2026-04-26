package com.demo.Store.service.impl;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.repository.ProductRepository;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.ProductService;
import com.demo.Store.util.StoreConstants;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.Validate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final VectorStore vectorStore;
    private final MessageSourceService messageSourceService;
    private final String PRODUCT_ID = "productId";


    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, VectorStore vectorStore, MessageSourceService messageSourceService) {
        this.productRepository = productRepository;
        this.vectorStore = vectorStore;
        this.messageSourceService = messageSourceService;
    }

    @Override
    public OperationResultDto addOrUpdate(Product product) {
        // 1. Save to relational DB
        Long productId = product.getId();
        if (Objects.nonNull(productId) && !productRepository.existsById(productId)) {
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("admin.products.ProductDoesNotExist")).build();
        }
        if (productRepository.existsDuplicate(product)) {
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("admin.products.ProductAlreadyExists")).build();
        }
        Product savedProduct = productRepository.save(product);
        try {
            String content = savedProduct.getName() + ". " + savedProduct.getAiDescription();
            Document doc = new Document(getVectorId(savedProduct.getId()), content, Map.of(PRODUCT_ID, savedProduct.getId()));
            vectorStore.add(List.of(doc));
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("admin.products.SmthWentWrong")).build();
        }
        return new OperationResultDto.Builder().valid(true).message(messageSourceService.getMessage("admin.products.ProductSaved")).build();
    }

    public String getVectorId(Long productId) {
        // Generates a deterministic UUID based on the bytes of your Long ID
        return UUID.nameUUIDFromBytes(String.valueOf(productId).getBytes()).toString();
    }

    @Override
    public List<Product> getAll(Integer page, Integer pageSize) {
        if (Objects.isNull(page) || Objects.isNull(pageSize)) {
            return productRepository.findAll();
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
    }

    @Override
    public Product getById(Long id) {
        Validate.notNull(id, "Product ID cannot be empty at this point!");
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> semanticSearch(String query, Integer page) {
        if (Objects.isNull(query) || query.isBlank()) {
            return getAll(page, StoreConstants.PRODUCT_SEARCH_CHUNK_SIZE);
        }
        // Search the vector store
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(StoreConstants.VECTOR_SEARCH_MAX_RESULTS).similarityThreshold(StoreConstants.PRODUCT_SIMILIARITY_THRESHOLD).build()
        );
        // Map vector documents back to relational entities
        List<Long> productIds = results.stream()
                .map(doc -> (Integer) doc.getMetadata().get(PRODUCT_ID)).map(Integer::longValue)
                .collect(Collectors.toList());
        return productRepository.findAllById(productIds);
    }

    @Override
    public OperationResultDto deleteById(Long id) {
        Validate.notNull(id, "Product id cannot be null at this point!");
        if (!productRepository.existsById(id)) {
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("admin.products.ProductDoesNotExist")).build();
        }
        productRepository.deleteById(id);
        String vectorId = getVectorId(id);
        vectorStore.delete(List.of(vectorId));
        return new OperationResultDto.Builder().valid(true).message(messageSourceService.getMessage("admin.products.ProductDeleted")).build();
    }

}
