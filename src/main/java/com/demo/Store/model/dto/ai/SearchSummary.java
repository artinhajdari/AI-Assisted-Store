package com.demo.Store.model.dto.ai;

import java.util.List;

public class SearchSummary {

    private List<Long> highlightedFeatureIDs;

    public List<Long> getHighlightedFeatureIDs() {
        return highlightedFeatureIDs;
    }

    public void setHighlightedFeatureIDs(List<Long> highlightedFeatureIDs) {
        this.highlightedFeatureIDs = highlightedFeatureIDs;
    }
}
