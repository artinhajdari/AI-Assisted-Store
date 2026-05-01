package com.demo.Store.model.dto.ai;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Objects;

public class SearchSummary {

    private List<Long> highlightedFeatureIDs;

    public SearchSummary(List<Long> highlightedFeatureIDs) {
        this.highlightedFeatureIDs = highlightedFeatureIDs;
    }

    public List<Long> getHighlightedFeatureIDs() {
        return highlightedFeatureIDs;
    }

    public void setHighlightedFeatureIDs(List<Long> highlightedFeatureIDs) {
        this.highlightedFeatureIDs = highlightedFeatureIDs;
    }

    public static SearchSummary empty() {
        return new SearchSummary(List.of());
    }

    public static <T extends AISearchable> SearchSummary of(List<T> elements)
    {
        if (Objects.isNull(elements) || elements.isEmpty()) {
            return empty();
        }
        return new SearchSummary(elements.stream().map(AISearchable::getId).toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SearchSummary that = (SearchSummary) o;

        return new EqualsBuilder().append(highlightedFeatureIDs, that.highlightedFeatureIDs).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(highlightedFeatureIDs).toHashCode();
    }
}
