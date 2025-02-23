package com.soubhagya.electronic.store.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;
/**
 * Represents a response message for API operations.
 * This class encapsulates the message details to be returned to the client,
 * including the message content, a success flag, and the HTTP status code.
 *
 * Attributes:
 * - message: Descriptive text about the API operation's result.
 * - success: A boolean flag indicating whether the API operation was successful.
 * - status: The HTTP status code associated with the API operation's response.
 */
//can be modified
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseMessage {

    private String message;
    private boolean success;
    private HttpStatus status;

}
