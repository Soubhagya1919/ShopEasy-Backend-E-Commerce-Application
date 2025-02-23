package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.dtos.ProductDto;
import com.soubhagya.electronic.store.entities.Category;
import com.soubhagya.electronic.store.entities.Product;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.helper.Helper;
import com.soubhagya.electronic.store.repositories.CategoryRepository;
import com.soubhagya.electronic.store.repositories.ProductRepository;
import com.soubhagya.electronic.store.services.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

/**
 * The ProductServiceImpl class provides an implementation for the ProductService interface,
 * handling CRUD operations and other business logic related to products.
 * It interacts with the ProductRepository for data persistence and uses ModelMapper for data transfer object conversions.
 * The service also manages product images in the local file system.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Value("${product.image.path}")
    private String imagePath;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Creates a new product based on the provided ProductDto object.
     * The method maps the ProductDto to a Product entity, assigns a unique ID,
     * sets the added date to the current date, and saves it to the repository.
     * Finally, it maps the saved Product entity back to a ProductDto.
     *
     * @param productDto the product data transfer object containing the details of the product to be created
     * @return the ProductDto representation of the newly created and saved product
     */
    @Override
    public ProductDto create(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        //product id
        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        //added date
        product.setAddedDate(new Date());
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    /**
     * Updates an existing product with the details provided in the productDto. The product is
     * identified by the given productId. If the product is not found, a ResourceNotFoundException
     * is thrown.
     *
     * @param productDto The DTO containing the updated product information such as title,
     *                   description, price, discounted price, quantity, live status, stock status,
     *                   and product image name.
     * @param productId  The unique identifier of the product to be updated.
     * @return A ProductDto representing the updated product.
     * @throws ResourceNotFoundException if no product is found with the given productId.
     */
    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id not found !!")
        );
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImageName(productDto.getProductImageName());

        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    /**
     * Deletes a product identified by the given productId. This method will remove the product
     * from the repository and also attempt to delete the associated product image from the file system.
     *
     * @param productId the unique identifier of the product to be deleted
     * @throws ResourceNotFoundException if no product is found with the given productId
     * @throws RuntimeException if there is an error during the deletion of the product image
     */
    @Override
    public void delete(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id not found !!")
        );
        String fullPath = imagePath + product.getProductImageName();

        Path path = Paths.get(fullPath);

        try {
            // Delete the user's profile image
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Product image deleted: {}", fullPath);
            } else {
                logger.warn("Product image not found: {}", fullPath);
            }
        } catch (IOException ex) {
            logger.error("Error occurred while deleting product image: {}", fullPath, ex);
            throw new RuntimeException("Failed to delete product image", ex);
        }
        productRepository.delete(product);
    }

    /**
     * Retrieves a ProductDto object based on the provided product ID.
     *
     * @param productId the unique identifier of the product to be retrieved
     * @return the ProductDto representation of the product
     * @throws ResourceNotFoundException if no product is found for the provided ID
     */
    @Override
    public ProductDto get(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id not found !!")
        );
        return modelMapper.map(product, ProductDto.class);
    }

    /**
     * Retrieves all products with pagination and sorting options.
     *
     * @param pageNumber the page number to retrieve, starting from 0
     * @param pageSize the number of products to retrieve per page
     * @param sortBy the property by which the products should be sorted
     * @param sortDir the direction of sorting; can be either "asc" for ascending
     *                or "desc" for descending
     * @return a PageableResponse containing a page of ProductDto objects
     */
    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    /**
     * Retrieves a paginated and sorted list of live products.
     *
     * @param pageNumber the page number to retrieve, zero-based
     * @param pageSize the number of products per page
     * @param sortBy the property to sort by
     * @param sortDir the direction to sort by, can be either "asc" for ascending or "desc" for descending
     * @return a PageableResponse containing a list of live ProductDto objects
     */
    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    /**
     * Searches for products with titles containing the given substring and returns a pageable response.
     *
     * @param subTitle the substring to search for within product titles
     * @param pageNumber the page number to retrieve
     * @param pageSize the number of items per page
     * @param sortBy the attribute to sort the results by
     * @param sortDir the direction of sorting, either "asc" for ascending or "desc" for descending
     * @return a pageable response containing a list of products that match the search criteria
     */
    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByTitleContaining(subTitle, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    /**
     * Creates a new product with the specified category.
     *
     * @param productDto the data transfer object containing the product details
     * @param categoryId the ID of the category to associate with the product
     * @return the created product data transfer object with updated information
     * @throws ResourceNotFoundException if the specified category does not exist
     */
    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        //fetch the category if it exists
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found !!"));

        Product product = modelMapper.map(productDto, Product.class);
        //product id
        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        //added date
        product.setAddedDate(new Date());
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);

    }

    /**
     * Updates the category of a product identified by the given product ID with a new category identified by the given category ID.
     *
     * @param productId the ID of the product to update
     * @param categoryId the ID of the new category to assign to the product
     * @return a ProductDto object representing the updated product with the new category
     * @throws ResourceNotFoundException if the product or category with the specified IDs are not found
     */
    @Override
    public ProductDto updateCategory(String productId, String categoryId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product of given id not found !!"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category of given id not found !!"));

        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDto.class);
    }

    /**
     * Retrieves a pageable response containing products belonging to a specific category.
     *
     * @param categoryId the identifier of the category for which products are to be retrieved
     * @param pageNumber the number of the page to retrieve
     * @param pageSize the number of items per page
     * @param sortBy the field by which the results should be sorted
     * @param sortDir the direction of sorting, either 'asc' for ascending or 'desc' for descending
     * @return a pageable response containing a list of ProductDto objects for the specified category
     * @throws ResourceNotFoundException if the category with the specified ID does not exist
     */
    @Override
    public PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("No such category found !!"));

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByCategory(category, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

}
