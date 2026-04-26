package com.demo.Store.service;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.OperationResultDto;

import java.util.List;

public interface ProductService {

    /**
     * Creates a new product or updates an existing one.
     *
     * @param product product entity containing values to persist
     * @return operation result describing whether persistence succeeded
     */
    OperationResultDto addOrUpdate(Product product);

    /**
     * Returns all products, optionally using paging arguments.
     *
     * @param page zero-based page index, or {@code null} to return all products
     * @param pageSize page size, or {@code null} to return all products
     * @return list of fetched products
     */
    List<Product> getAll(Integer page, Integer pageSize);

    /**
     * Fetches a single product by its identifier.
     *
     * @param id product identifier
     * @return matching product or {@code null} when it does not exist
     */
    Product getById(Long id);

    /**
     * Searches products semantically by using vector similarity.
     *
     * @param query search text used for semantic matching
     * @param page page index used by fallback pagination when query is blank
     * @return list of products matching the semantic query
     */
    List<Product> semanticSearch(String query, Integer page);

    /**
     * Deletes a product by its identifier.
     *
     * @param id product identifier to delete
     * @return operation result describing whether deletion succeeded
     */
    OperationResultDto deleteById(Long id);
}
