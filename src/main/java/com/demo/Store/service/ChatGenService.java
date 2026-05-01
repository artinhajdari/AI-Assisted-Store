package com.demo.Store.service;

import com.demo.Store.model.dto.ai.AISearchable;
import com.demo.Store.model.dto.ai.SearchSummary;

import java.util.List;

public interface ChatGenService {

    /**
     * Filters AI-searchable elements based on a user prompt by using the configured chat model.
     *
     * @param userPrompt prompt text that describes what the user is searching for
     * @param elements   candidate elements that can be filtered by relevance
     * @return {@link SearchSummary} containing list of IDs of elements that best match the prompt
     * @throws Exception when prompt generation, model invocation, or response parsing fails
     */
    <T extends AISearchable> SearchSummary searchElements(String userPrompt, List<T> elements) throws Exception;

}
