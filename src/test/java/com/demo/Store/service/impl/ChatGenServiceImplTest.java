package com.demo.Store.service.impl;

import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.ai.SearchSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatGenServiceImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatModel chatModel;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChatGenServiceImpl service;

    private List<ProductDto> productDtos;

    @BeforeEach
    void setUp() {
        ProductDto productDto1 = new ProductDto();
        productDto1.setId(1L);
        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDtos = List.of(productDto1, productDto2);
    }

    @Test
    void filterElementsReturnsOriginalListWhenPromptIsBlank() throws Exception {
        List<Long> result = unwrapModelResponse(service.searchElements(" ", productDtos));

        assertEquals(2, result.size());
        verifyNoInteractions(chatModel);
    }

    @Test
    void filterElementsReturnsEmptyListWhenModelResponseIsNull() throws Exception {
        when(chatModel.call(any(Prompt.class)).getResult().getOutput().getText()).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn("");

        SearchSummary result = service.searchElements("laptop", productDtos);

        assertEquals(SearchSummary.empty(), result);
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    void filterElementsFiltersByReturnedIds() throws Exception {
        when(chatModel.call(any(Prompt.class)).getResult().getOutput().getText())
                .thenReturn("{\"highlightedFeatureIDs\":[2]}");
        when(objectMapper.writeValueAsString(any())).thenReturn("");

        List<Long> result = unwrapModelResponse(service.searchElements("show me second", productDtos));

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst());
    }

    @Test
    void filterElementsReturnsEmptyWhenElementsAreMissing() throws Exception {
        List<Long> result = unwrapModelResponse(service.searchElements("query", List.of()));

        assertEquals(0, result.size());
        verify(chatModel, never()).call(any(Prompt.class));
    }

    private List<Long> unwrapModelResponse(SearchSummary response) {
        return Optional.ofNullable(response).map(SearchSummary::getHighlightedFeatureIDs).orElse(List.of());
    }
}