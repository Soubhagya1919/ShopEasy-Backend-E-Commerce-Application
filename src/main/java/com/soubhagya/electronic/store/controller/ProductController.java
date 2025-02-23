package com.soubhagya.electronic.store.controller;

import com.soubhagya.electronic.store.dtos.*;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
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

/**
 * ProductController manages CRUD operations and image handling for products.
 * It provides endpoints to create, update, delete, and retrieve products, as well as
 * to handle product images.
 */
@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "scheme1")
public class ProductController {

    /**
     * An instance of ProductService used to manage and provide operations related
     * to product management within the system. This variable is initialized as a
     * final instance, ensuring that the reference to the ProductService is immutable
     * after initial assignment, promoting consistent and reliable access to product-related
     * functionalities throughout the application's lifecycle.
     */
    private final ProductService productService;

    /**
     * An instance of FileService responsible for handling file-related operations.
     * This service may include functionalities such as reading from and writing to files,
     * managing file directories, and ensuring file integrity and security.
     * Being marked as final, this instance cannot be reassigned after initialization,
     * ensuring consistent use of the same FileService throughout the lifecycle of the encapsulating class.
     */
    private final FileService fileService;

    /**
     * Represents the file path to the directory where product images are stored.
     * The value is injected from an external configuration property
     * specified by 'product.image.path'. This allows the file path
     * to be dynamically configured at runtime, providing flexibility
     * in managing and accessing product image resources.
     */
    @Value("${product.image.path}")
    private String imagePath;

    /**
     * A logger for recording events, information, and debugging messages within the application.
     * Utilizes the SLF4J LoggerFactory to obtain a logger instance that corresponds to the runtime class of the current object.
     * This enables consistent and class-specific logging, aiding in monitoring and troubleshooting application performance and behavior.
     * As a final variable, it ensures that the logger instance is immutable and associated only with this specific class context.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructs a new ProductController with the specified services.
     *
     * @param productService the service responsible for handling product-related operations
     * @param fileService the service responsible for handling file-related operations
     */
    public ProductController(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    /**
     * Handles the HTTP POST request for creating a new product.
     * Validates the request body and delegates the creation to the ProductService.
     *
     * @param productDto a ProductDto object containing the details of the product to be created
     * @return a ResponseEntity containing the created ProductDto and an HTTP status code of CREATED
     */
    //create
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto){
        ProductDto createdProduct = productService.create(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Updates an existing product with the given information.
     *
     * @param productDto the product data transfer object containing updated details
     * @param productId the unique identifier of the product to be updated
     * @return a ResponseEntity containing the updated ProductDto and HTTP status OK
     */
    //update
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto, @PathVariable String productId){
        ProductDto updatedProduct = productService.update(productDto, productId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Handles HTTP DELETE requests to remove a product by its ID.
     *
     * @param productId the unique identifier of the product to be deleted
     * @return a ResponseEntity containing an ApiResponseMessage indicating the deletion status
     */
    //delete
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId){
        productService.delete(productId);

        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().
                message("Product deleted Successfully !!").
                success(true).
                status(HttpStatus.OK).build();

        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to retrieve
     * @return a ResponseEntity containing the ProductDto and HTTP status OK
     */
    //get single
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId){
        ProductDto productDto = productService.get(productId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of all products with optional sorting.
     *
     * @param pageNumber the index of the page to retrieve, starting from 0; defaults to 0 if not provided
     * @param pageSize the number of products per page; defaults to 10 if not provided
     * @param sortBy the attribute by which to sort the products; defaults to "title" if not provided
     * @param sortDir the direction of sorting, either "asc" for ascending or "desc" for descending; defaults to "asc" if not provided
     * @return a ResponseEntity containing a pageable response of ProductDto objects with an HTTP status of OK
     */
    //get all
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }
    /**
     * Retrieves a paginated list of live products based on the provided pagination and sorting parameters.
     *
     * @param pageNumber the page number to retrieve, defaults to 0 if not specified
     * @param pageSize the number of records per page, defaults to 10 if not specified
     * @param sortBy the field by which to sort the results, defaults to "title" if not specified
     * @param sortDir the direction of sorting, either 'asc' for ascending or 'desc' for descending, defaults to 'asc' if not specified
     * @return a ResponseEntity containing a PageableResponse of ProductDto objects and the HTTP status
     */
    //get all live
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }
    /**
     * Searches for products based on a query string.
     * Supports pagination and sorting.
     *
     * @param query the query string to search products by title
     * @param pageNumber the page number for pagination, optional and defaults to 0
     * @param pageSize the size of the page for pagination, optional and defaults to 10
     * @param sortBy the attribute by which the results should be sorted, optional and defaults to "title"
     * @param sortDir the direction of sorting, either "asc" or "desc", optional and defaults to "asc"
     * @return a ResponseEntity containing a pageable response of ProductDto objects
     */
    //search all
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> pageableResponse = productService.searchByTitle(query, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    /**
     * Uploads an image for a specified product and updates the product's image data.
     *
     * @param productId the unique identifier of the product for which the image is being uploaded
     * @param image the image file to be uploaded for the product
     * @return a ResponseEntity containing an ImageResponse with details about the upload status
     * @throws IOException if an error occurs during file upload
     */
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage")MultipartFile image
            ) throws IOException {
        String fileName = fileService.uploadFile(image, imagePath);
        ProductDto productDto = productService.get(productId);

        productDto.setProductImageName(fileName);
        ProductDto updatedProduct = productService.update(productDto, productId);

        ImageResponse response = ImageResponse.builder().
                imageName(fileName).
                message("Product image uploaded successfully !!").
                status(HttpStatus.CREATED).
                success(true).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Serves a product image to the client through the HTTP response.
     *
     * @param productId the unique identifier of the product whose image is to be served
     * @param response the HttpServletResponse used to write the image data
     * @throws FileNotFoundException if the product or its image is not found
     */
    @GetMapping("/image/{productId}")
    public void serveUserImage(@PathVariable String productId, HttpServletResponse response) throws FileNotFoundException {

        ProductDto productDto = productService.get(productId);
        if (productDto == null || productDto.getProductImageName() == null) {
            throw new ResourceNotFoundException("Product or image not found");
        }

        logger.info("Product image name: {}", productDto.getProductImageName());

        try (InputStream resource = fileService.getResource(imagePath, productDto.getProductImageName())) {
            String fileExtension = getFileExtension(productDto.getProductImageName());
            response.setContentType(getMediaTypeForExtension(fileExtension));
            StreamUtils.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error serving product image", e);
        }
    }

    /**
     * Retrieves the file extension from a given file name.
     *
     * @param fileName the name of the file from which to extract the extension
     * @return the file extension, or an empty string if there is no extension
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex != -1) ? fileName.substring(lastDotIndex + 1) : "";
    }

    /**
     * Determines the media type for a given file extension.
     *
     * @param extension the file extension for which the media type is to be identified
     * @return the corresponding media type as a string such as "image/png" for "png",
     *         "image/jpeg" for "jpeg" and "jpg", or "application/octet-stream" for unknown extensions
     */
    private String getMediaTypeForExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "jpeg", "jpg" -> MediaType.IMAGE_JPEG_VALUE;
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

}
