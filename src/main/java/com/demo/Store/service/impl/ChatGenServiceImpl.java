package com.demo.Store.service.impl;

import com.demo.Store.model.dto.ai.AISearchable;
import com.demo.Store.model.dto.ai.SearchSummary;
import com.demo.Store.service.ChatGenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatGenServiceImpl implements ChatGenService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatGenServiceImpl(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T extends AISearchable> SearchSummary searchElements(String userPrompt, List<T> elements) throws Exception {
        if (StringUtils.isBlank(userPrompt) || Objects.isNull(elements) || elements.isEmpty()) {
            return SearchSummary.of(elements);
        }
        var converter = new BeanOutputConverter<>(SearchSummary.class);
        String jsonItems = objectMapper.writeValueAsString(elements);
        String promptText = """
            Review the following items: {elements}
            Based on the user's request: "{query}", select the best matches.
            Only output the IDs of the best matching items.
            {format}
            """;
        Prompt prompt = new Prompt(new PromptTemplate(promptText)
                .render(Map.of(
                        "elements", jsonItems,
                        "query", userPrompt,
                        "format", converter.getFormat()
                )));
        String response = chatModel.call(prompt).getResult().getOutput().getText();
        return Optional.ofNullable(response).map(res -> converter.convert(response)).orElse(SearchSummary.empty());
    }
}
