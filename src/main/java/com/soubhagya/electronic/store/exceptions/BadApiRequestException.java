package com.soubhagya.electronic.store.exceptions;

/**
 * Exception thrown to indicate that a request to an API endpoint is malformed or invalid.
 * This exception is typically used in scenarios where the request does not meet
 * the expected criteria, such as having an unsupported format or missing required parameters.
 *
 * BadApiRequestException extends RuntimeException, allowing it to be thrown
 * without being explicitly declared in a method's throws clause.
 */
public class BadApiRequestException extends RuntimeException{
    public BadApiRequestException() {
        super("Bad Request !!");
    }

    public BadApiRequestException(String message) {
        super(message);
    }
}
