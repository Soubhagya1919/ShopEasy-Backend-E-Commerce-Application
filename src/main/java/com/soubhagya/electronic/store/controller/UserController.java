package com.soubhagya.electronic.store.controller;

import com.soubhagya.electronic.store.constants.Providers;
import com.soubhagya.electronic.store.dtos.ApiResponseMessage;
import com.soubhagya.electronic.store.dtos.ImageResponse;
import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.dtos.UserDto;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.services.FileService;
import com.soubhagya.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * UserController is a REST controller for managing user-related actions.
 * It provides endpoints for creating, updating, deleting, retrieving, and searching for users.
 * Additionally, it includes functionality for managing user profile images.
 * Security requirements and API documentation annotations are applied at the class level.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "UserController", description = "REST APIs related to performing user related actions")
@SecurityRequirement(name = "scheme1")
public class UserController {

    /**
     * A reference to the UserService instance that is responsible for
     * managing user-related operations such as creation, deletion,
     * modification, and retrieval of user information. This service
     * acts as an intermediary between the application and the user
     * data repository, ensuring that all user management functionalities
     * are executed through this centralized service layer.
     */
    private final UserService userService;

    /**
     * An instance of {@code FileService} used for handling operations related to file management.
     * This object provides methods to perform actions such as reading from, writing to,
     * and deleting files within the application. It is initialized once and used across
     * the system to ensure consistent file handling procedures.
     */
    private final FileService fileService;

    /**
     * The {@code logger} is a {@link Logger} instance used for logging events
     * related to the {@code UserController} class. It provides methods to log
     * various levels of messages (e.g., debug, info, warn, error) which assist
     * in monitoring and debugging the application's operations and behaviors
     * within the context of handling user-related requests and actions.
     * The logger is initialized using a {@link LoggerFactory} to ensure
     * consistent and efficient logging practices aligned with the logging
     * framework being utilized.
     */
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Represents the file system path where user profile images are uploaded.
     * The value of this variable is injected from an external configuration property
     * defined under 'user.profile.image.path'.
     * This path is used to store and retrieve profile images corresponding to user accounts.
     */
    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    /**
     * Constructs a new UserController with the specified UserService and FileService.
     *
     * @param userService the service used for user-related operations
     * @param fileService the service used for file-related operations
     */
    public UserController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    /**
     * Creates a new user with the provided user information.
     *
     * @param userDto the user data transfer object containing user details
     * @return a ResponseEntity containing the created UserDto and the HTTP status code 201 (Created)
     */
    //create
    @PostMapping
    @Operation(summary = "create new user !!", description = "this is user api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success | OK"),
            @ApiResponse(responseCode = "401", description = "nto authorized !!"),
            @ApiResponse(responseCode = "201", description = "new user created !!")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        userDto.setProvider(Providers.SELF);
        UserDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Updates an existing user's information based on the provided user ID and user data.
     *
     * @param userId the unique identifier of the user to be updated
     * @param userDto the user data transfer object containing updated user information
     * @return a ResponseEntity containing the updated UserDto object and an HTTP status code of OK
     */
    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserDto userDto
    ){
        UserDto updatedUser = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Deletes a user based on the provided user ID.
     *
     * @param userId the unique identifier of the user to be deleted
     * @return a ResponseEntity containing an ApiResponseMessage indicating the result of the deletion operation
     */
    //delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder().
                message("User is deleted successfully !!").
                success(true).
                status(HttpStatus.OK).build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageNumber the page number to retrieve, default is 0 if not specified
     * @param pageSize the number of users per page, default is 10 if not specified
     * @param sortBy the field by which to sort the user list, default is "name" if not specified
     * @param sortDir the direction of sorting, either "asc" for ascending or "desc" for descending, default is "asc" if not specified
     * @return a ResponseEntity containing a PageableResponse of UserDto objects
     */
    //get-all
    @GetMapping
    @Operation(summary = "get all users")
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber",
                    defaultValue = "0",
                    required = false) int pageNumber,
            @RequestParam(value = "pageSize",
                    defaultValue = "10",
                    required = false) int pageSize,
            @RequestParam(value = "sortBy",
                    defaultValue = "name",
                    required = false) String sortBy,
            @RequestParam(value = "sortDir",
                    defaultValue = "asc",
                    required = false) String sortDir
    ){
        return new ResponseEntity<>(userService.getAllUser(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    /**
     * Retrieves a user by their unique user ID.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a ResponseEntity containing the UserDto object of the requested user
     */
    //get single
    @GetMapping("/{userId}")
    @Operation(summary = "Get single user by userid !!")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to be retrieved
     * @return a ResponseEntity containing the UserDto object and HTTP status OK
     */
    //get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    /**
     * Searches for users based on the provided keywords.
     *
     * @param keywords A string representing the keywords to search for users.
     * @return A ResponseEntity containing a list of UserDto objects that match the search criteria,
     *         along with an HTTP status code indicating the outcome of the operation.
     */
    //search user
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords){
        return new ResponseEntity<>(userService.searchUser(keywords), HttpStatus.OK);
    }

    /**
     * Uploads a user's image to the server and updates the user's profile with the image name.
     *
     * @param image   the image file to be uploaded
     * @param userId  the unique identifier of the user whose image is being uploaded
     * @return a ResponseEntity containing an ImageResponse, which includes the image name,
     *                success status, message, and HTTP status
     * @throws IOException if an input or output exception occurred
     */
    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);
        //updating the user profile
        UserDto userDto = userService.getUserById(userId);
        userDto.setImageName(imageName);

        userService.updateUser(userDto, userId);

        ImageResponse imageResponse =
                ImageResponse.builder().
                        imageName(imageName).
                        success(true).
                        message("Image uploaded successfully").
                        status(HttpStatus.CREATED).build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    /**
     * Serves the user image based on the supplied user ID. It retrieves the user's image
     * from the file system and writes it to the HTTP response.
     *
     * @param userId the ID of the user whose image is to be served
     * @param response the HttpServletResponse object used to write the image data
     * @throws FileNotFoundException if the user's image is not found
     */
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws FileNotFoundException {
        //og code

//        UserDto user = userService.getUserById(userId);
//        logger.info("User image name: {} ", user.getImageName());
//        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
//
//        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//        try {
//            StreamUtils.copy(resource, response.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        //modified code

        UserDto user = userService.getUserById(userId);
        if (user == null || user.getImageName() == null) {
            throw new ResourceNotFoundException("User or image not found");
        }

        logger.info("User image name: {}", user.getImageName());

        try (InputStream resource = fileService.getResource(imageUploadPath, user.getImageName())) {
            String fileExtension = getFileExtension(user.getImageName());
            response.setContentType(getMediaTypeForExtension(fileExtension));
            StreamUtils.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error serving user image", e);
        }
    }

    /**
     * Extracts the file extension from a given file name.
     * The method searches for the last dot in the file name and returns the substring
     * following this dot as the file extension. If no dot is found, it returns an empty string.
     *
     * @param fileName the name of the file from which to extract the file extension
     * @return the file extension without the dot, or an empty string if no dot is found
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex != -1) ? fileName.substring(lastDotIndex + 1) : "";
    }

    /**
     * Determines the media type for a given file extension.
     *
     * @param extension the file extension to get the media type for
     * @return the corresponding media type as a String. Returns "image/png" for "png",
     *         "image/jpeg" for "jpeg" or "jpg", and "application/octet-stream" for unknown extensions.
     */
    private String getMediaTypeForExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "jpeg", "jpg" -> MediaType.IMAGE_JPEG_VALUE;
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }
}
