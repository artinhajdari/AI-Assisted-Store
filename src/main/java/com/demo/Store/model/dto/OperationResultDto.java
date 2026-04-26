package com.demo.Store.model.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OperationResultDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean valid;
    private String message;

    public OperationResultDto() {}

    public OperationResultDto(Builder builder) {
        this.valid = builder.valid;
        this.message = builder.message;
    }

    public static class Builder {
        private Boolean valid;
        private String message;

        public Builder valid(Boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public OperationResultDto build() {
            return new OperationResultDto(this);
        }

    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OperationResultDto that = (OperationResultDto) o;

        return new EqualsBuilder().append(valid, that.valid).append(message, that.message).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(valid).append(message).toHashCode();
    }
}
