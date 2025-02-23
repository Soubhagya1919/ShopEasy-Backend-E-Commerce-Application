package com.soubhagya.electronic.store.exceptions;

import lombok.Builder;

/**
 * ResourceNotFoundException is thrown when a specified resource cannot be found.
 * This exception is typically used in scenarios where an application attempts to
 * access a resource that does not exist or is not accessible.
 *
 * This exception extends RuntimeException, meaning it is an unchecked exception
 * and does not need to be declared in a method's throws clause.
 *
 * It offers two constructors: one without parameters that uses a default error
 * message, and another that accepts a custom error message.
 */
@Builder
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException() {
        super("Resource not found !!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
