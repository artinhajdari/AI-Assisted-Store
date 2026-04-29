package com.demo.Store.model.dto;

import com.demo.Store.model.dto.ai.AISearchable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProductDto implements AISearchable {

    private Long id;
    @NotBlank(message = "{admin.products.NameRequired}")
    private String name;
    @NotBlank(message = "{admin.products.DetailedDescriptionRequired}")
    private String aiDescription;
    @NotBlank(message = "{admin.products.SimpleDescriptionRequired}")
    private String displayedDescription;
    @NotNull(message = "{admin.products.PriceRequired}")
    private Double price;
    @NotNull(message = "{admin.products.QuantityRequired}")
    private Integer quantity;
    private String imageUrl;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAiDescription() {
        return aiDescription;
    }

    public void setAiDescription(String description) {
        this.aiDescription = description;
    }

    public String getDisplayedDescription() {
        return displayedDescription;
    }

    public void setDisplayedDescription(String displayedDescription) {
        this.displayedDescription = displayedDescription;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProductDto that = (ProductDto) o;

        return new EqualsBuilder().append(id, that.id).append(name, that.name).append(aiDescription, that.aiDescription).append(displayedDescription, that.displayedDescription).append(price, that.price).append(quantity, that.quantity).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(name).append(aiDescription).append(displayedDescription).append(price).append(quantity).toHashCode();
    }
}
