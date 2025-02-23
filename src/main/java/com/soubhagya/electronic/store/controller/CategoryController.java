package com.soubhagya.electronic.store.controller;

import com.soubhagya.electronic.store.dtos.*;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.services.CategoryService;
import com.soubhagya.electronic.store.services.FileService;
import com.soubhagya.electronic.store.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * REST controller for managing categories and related operations.
 */
@RestController
@RequestMapping("/categories")
@SecurityRequirement(name = "scheme1")
public class CategoryController {

    /**
     * A service dedicated to managing and retrieving category-related data.
     * This instance provides methods to interact with and manipulate
     * category entities within the application. It encapsulates the
     * business logic for category operations and serves as an intermediary
     * between the controller and the data model for categories.
     */
    private final CategoryService categoryService;

    /**
     * An instance of the ProductService class that is utilized for handling
     * operations related to product management within the application.
     * It is used to interact with and fetch data about products, perform
     * business logic, and serve as a mediator between the product data and
     * application logic.
     *
     * This service typically includes methods for creating, updating,
     * deleting, and retrieving product information. Being declared as
     * 'final' implies that the reference to this ProductService instance
     * cannot be changed once it is initialized, ensuring the integrity and
     * consistent use of this service throughout its lifecycle in the
     * application.
     */
    private final ProductService productService;

    /**
     * A logger instance used for logging messages and events.
     * This logger is configured to capture logs for the current class.
     * Utilizing SLF4J with an underlying logging framework, it allows for
     * generating log messages at various levels such as DEBUG, INFO, WARN, ERROR.
     * The logger is initialized with the class's name to enable contextual logging
     * and facilitate easier troubleshooting and monitoring.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * A service component instance responsible for handling file operations.
     * This instance provides the necessary functionalities to perform
     * tasks such as reading from and writing to files, managing file
     * storage, and potentially interfacing with remote file systems.
     * As a final variable, it is instantiated at the time of object
     * creation and its reference cannot be changed thereafter.
     */
    private final FileService fileService;

    /**
     * Represents the file system path designated for uploading category images.
     * This variable is injected from a configuration source, typically an
     * application properties file, and retrieves its value using the
     * "@Value" annotation with the key "category.image.path". The path allows
     * the application to store or access images related to different categories
     * within the application context.
     */
    @Value("${category.image.path}")
    private String imageUploadPath;

    /**
     * Constructs a new CategoryController with the specified services.
     *
     * @param categoryService an instance of CategoryService to manage category operations
     * @param fileService an instance of FileService to handle file-related operations
     * @param productService an instance of ProductService to manage product operations
     */
    public CategoryController(CategoryService categoryService, FileService fileService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.fileService = fileService;
    }

    /**
     * Handles the HTTP POST request to create a new category using the provided category data.
     *
     * @param categoryDto the data transfer object containing the details of the category to be created
     * @return a ResponseEntity containing the created CategoryDto and an HTTP status of CREATED
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto category = categoryService.create(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    /**
     * Updates an existing category with the provided category data.
     *
     * @param categoryDto the data transfer object containing updated category information
     * @param categoryId the unique identifier of the category to be updated
     * @return a ResponseEntity containing the updated CategoryDto and an HTTP status code
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @RequestBody @Valid CategoryDto categoryDto,
            @PathVariable String categoryId
    )
    {
        CategoryDto category = categoryService.update(categoryDto, categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    /**
     * Deletes a category identified by the given categoryId.
     *
     * @param categoryId the ID of the category to be deleted
     * @return a ResponseEntity containing an ApiResponseMessage indicating the result of the delete operation
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId){
        categoryService.delete(categoryId);
        ApiResponseMessage response = ApiResponseMessage
                .builder().
                message("Category is deleted successfully !!").
                status(HttpStatus.OK).
                success(true).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated and sorted list of categories.
     *
     * @param pageNumber the index of the page to be retrieved, default is 0
     * @param pageSize the number of records per page, default is 10
     * @param sortBy the field by which to sort the records, default is "title"
     * @param sortDir the direction of sorting, can be either "asc" or "desc", default is "asc"
     * @return a ResponseEntity containing a PageableResponse of CategoryDto objects
     */
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<CategoryDto> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a single category based on the provided category ID.
     *
     * @param categoryId the unique identifier of the category to retrieve
     * @return a ResponseEntity containing the CategoryDto object for the specified category if found;
     *         otherwise, an appropriate error response
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getSingle(@PathVariable String categoryId){
        CategoryDto categoryDto = categoryService.get(categoryId);
        return ResponseEntity.ok(categoryDto);
    }
/**
 * Handles the uploading of a category image and updates the category profile with the uploaded image.
 *
 * @param image the MultipartFile representing the image to be uploaded
 * @param categoryId the ID of the category for which the image is being uploaded
 * @return a ResponseEntity containing an ImageResponse with details about the outcome of the operation
 * @throws IOException if an input or output exception occurs during file upload
 */
//New update
    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryImage(
            @RequestParam("categoryImage") MultipartFile image,
            @PathVariable String categoryId) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);
        //updating the category profile
        CategoryDto categoryDto = categoryService.get(categoryId);
        categoryDto.setCoverImage(imageName);

        categoryService.update(categoryDto, categoryId);

        ImageResponse imageResponse =
                ImageResponse.builder().
                        imageName(imageName).
                        success(true).
                        message("Image uploaded successfully").
                        status(HttpStatus.CREATED).build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    /**
     * Serves the cover image for a specified category.
     *
     * @param categoryId the ID of the category whose image is to be served
     * @param response the HttpServletResponse object used to write the image data
     * @throws FileNotFoundException if the file corresponding to the category image is not found
     */
    @GetMapping("/image/{categoryId}")
    public void serveCategoryImage(@PathVariable String categoryId, HttpServletResponse response) throws FileNotFoundException {
        CategoryDto categoryDto = categoryService.get(categoryId);
        if (categoryDto == null || categoryDto.getCoverImage() == null) {
            throw new ResourceNotFoundException("Category or image not found");
        }

        logger.info("Category image name: {}", categoryDto.getCoverImage());

        try (InputStream resource = fileService.getResource(imageUploadPath, categoryDto.getCoverImage())) {
            String fileExtension = getFileExtension(categoryDto.getCoverImage());
            response.setContentType(getMediaTypeForExtension(fileExtension));
            StreamUtils.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error serving category image", e);
        }
    }

    /**
     * Extracts and returns the file extension from the provided file name.
     *
     * @param fileName the name of the file from which to extract the extension
     * @return the file extension if present; an empty string if the file name does not contain an extension
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex != -1) ? fileName.substring(lastDotIndex + 1) : "";
    }

    /**
     * Determines the media type for a given file extension.
     *
     * @param extension the file extension for which the media type is to be determined
     * @return a string representing the media type associated with the given extension.
     *         Returns "image/png" for "png" extension, "image/jpeg" for "jpeg" or "jpg" extensions,
     *         and "application/octet-stream" for any other extension.
     */
    private String getMediaTypeForExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "jpeg", "jpg" -> MediaType.IMAGE_JPEG_VALUE;
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

    /**
     * Searches for categories based on the provided keyword, which is expected to match the title or description
     * of the categories.
     *
     * @param keyword the search term that will be used to find matching categories by title or description
     * @return ResponseEntity containing a list of CategoryDto objects that match the search criteria, encapsulated
     *         in an HTTP status code of OK
     */
    //search category based on title or desc
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<CategoryDto>> searchCategories(@PathVariable String keyword) {
        List<CategoryDto> categories = categoryService.searchCategories(keyword);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Creates a new product and associates it with an existing category.
     *
     * @param categoryId the identifier of the category with which the product is to be associated
     * @param productDto the data transfer object containing the details of the product to be created
     * @return a ResponseEntity containing the created product details and the HTTP status code
     */
    //create product with category
    @PostMapping("/{categoryId}/products")
    public  ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable String categoryId,
            @RequestBody ProductDto productDto
    ) {

        ProductDto productWithCategory = productService.createWithCategory(productDto, categoryId);
        return new ResponseEntity<>(productWithCategory, HttpStatus.CREATED);
    }

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     *
     * @param categoryId the unique identifier of the category for which products are to be retrieved
     * @param pageNumber the number of the page to retrieve, default is 0
     * @param pageSize the number of products per page, default is 10
     * @param sortBy the property by which to sort the products, default is "title"
     * @param sortDir the direction of the sort, can be "asc" for ascending or "desc" for descending, default is "asc"
     * @return a ResponseEntity containing a PageableResponse of ProductDto objects and an HTTP status code of OK
     */
    //update category of product
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategory(
            @PathVariable String categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {

        PageableResponse<ProductDto> response = productService.getAllOfCategory(categoryId, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
