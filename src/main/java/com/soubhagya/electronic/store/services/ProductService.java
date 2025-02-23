package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.dtos.ProductDto;

/**
 * Service interface for managing products within an electronic store.
 * This interface provides methods to handle various operations related
 * to creating, updating, deleting, and retrieving products, as well as
 * searching and managing product categories.
 */
public interface ProductService {

    //create
    ProductDto create(ProductDto productDto);

    //update
    ProductDto update(ProductDto productDto, String productId);

    //delete
    void delete(String productId);

    //get single
    ProductDto get(String productId);

    //get all
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get all : live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);

    ///search product
    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir);

    //create product with category
    ProductDto createWithCategory(ProductDto productDto, String categoryId);

    //update category of product
    ProductDto updateCategory(String productId, String categoryId);

    //get products of a single category
    PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);

    //other methods
}
