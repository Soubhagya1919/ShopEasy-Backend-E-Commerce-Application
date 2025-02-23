package com.soubhagya.electronic.store.exceptions;

import com.soubhagya.electronic.store.dtos.ApiResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandling is a centralized exception handler for a Spring Boot application.
 * It intercepts exceptions thrown during the execution of requests and provides
 * customized responses to the client.
 *
 * The class handles the following exceptions:
 *
 * 1. ResourceNotFoundException: Returns a 404 Not Found response with a message from the exception.
 * 2. MethodArgumentNotValidException: Returns a 400 Bad Request response containing a map
 *    with field errors that occurred during validation, where each entry corresponds to a failed field.
 * 3. BadApiRequestException: Returns a 400 Bad Request response with a message from the exception.
 *
 * This class uses the @RestControllerAdvice annotation to allow exception handling
 * throughout the application's controller classes. Logging is performed to note when
 * specific handlers are invoked.
 */
@RestControllerAdvice
public class GlobalExceptionHandling {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handles exceptions of type ResourceNotFoundException by providing a customized JSON response.
     * The response includes details specified in the ApiResponseMessage such as the message content,
     * a success flag set to true, and an HTTP status code of NOT_FOUND.
     *
     * @param ex the exception that was thrown when a resource could not be found
     * @return a ResponseEntity containing ApiResponseMessage detailing the error, with HTTP status 404 (NOT_FOUND)
     */
    //handle resource not found exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){

        logger.info("Exception Handler invoked !!");
        ApiResponseMessage response = ApiResponseMessage.builder().
                message(ex.getMessage()).
                status(HttpStatus.NOT_FOUND).
                success(true).
                build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions of type MethodArgumentNotValidException, which are thrown
     * when validation on an argument annotated with @Valid fails.
     *
     * @param ex the MethodArgumentNotValidException thrown during argument validation failure.
     * @return a ResponseEntity containing a map with field names as keys and validation error
     *         messages as values, along with a 400 Bad Request HTTP status.
     */
    //MethodArgumentNotValidException
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        Map<String, Object> response = new HashMap<>();
        allErrors.forEach(objectError -> {
            String message = objectError.getDefaultMessage();
            String field = ((FieldError)objectError).getField();
            response.put(field, message);
        });

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

//        Map<String, String> fieldErrors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .collect(Collectors.toMap(
//                        FieldError::getField,
//                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
//                ));
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                "VALIDATION_FAILED",
//                "Validation failed for one or more fields",
//                fieldErrors
//        );
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions of type BadApiRequestException, which occur when an API request is malformed or invalid.
     * This method logs the occurrence of the exception and constructs an ApiResponseMessage indicating
     * the failure of the request, along with an appropriate HTTP status code and message.
     *
     * @param ex the BadApiRequestException that was thrown due to a malformed or invalid API request
     * @return a ResponseEntity containing an ApiResponseMessage with details of the error,
     *         specifically an HTTP status of BAD_REQUEST and success flag as false
     */
    //handle bad api exception
    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<ApiResponseMessage> handleBadApiRequest(BadApiRequestException ex){

        logger.info("Bad Api Request !!");
        ApiResponseMessage response = ApiResponseMessage.builder().
                message(ex.getMessage()).
                status(HttpStatus.BAD_REQUEST).
                success(false).
                build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
