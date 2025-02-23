package com.soubhagya.electronic.store.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;
/**
 * Represents the response of an image-related operation within the system.
 * This class contains details about the result of the operation, such as
 * the name of the image involved, a descriptive message, the success status,
 * and the HTTP status code.
 *
 * Attributes:
 * - imageName: The name of the image involved in the operation.
 * - message: A descriptive message regarding the operation's result.
 * - success: A boolean flag indicating whether the image operation was successful.
 * - status: The HTTP status code corresponding to the outcome of the operation.
 */
//can be modified
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    private String imageName;
    private String message;
    private boolean success;
    private HttpStatus status;

}
