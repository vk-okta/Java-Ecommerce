package com.ecommerce.javaecom.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String message;
    Long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String field, String message) {
        super(String.format("%s not found with %s: %s", resourceName, field, message));
        this.resourceName = resourceName;
        this.field = field;
        this.message = message;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

}
