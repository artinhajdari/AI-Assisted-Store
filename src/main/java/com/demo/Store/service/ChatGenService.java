package com.demo.Store.service;

import com.demo.Store.model.dto.ai.AISearchable;

import java.util.List;

public interface ChatGenService {

    /**
     * Filters AI-searchable elements based on a user prompt by using the configured chat model.
     *
     * @param userPrompt prompt text that describes what the user is searching for
     * @param elements candidate elements that can be filtered by relevance
     * @return filtered list of elements that best match the prompt
     * @param <T> concrete element type implementing {@link AISearchable}
     * @throws Exception when prompt generation, model invocation, or response parsing fails
     */
     <T extends AISearchable> List<T> filterElements(String userPrompt, List<T> elements) throws Exception;

}
