package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.CategoryDto;
import com.soubhagya.electronic.store.dtos.PageableResponse;

import java.util.List;

/**
 * Provides the service operations for managing product categories.
 * This interface defines methods for creating, updating, deleting,
 * retrieving, and searching for product categories in the system.
 */
public interface CategoryService {

    //create
    CategoryDto create(CategoryDto categoryDto);

    //update
    CategoryDto update(CategoryDto categoryDto, String categoryId);

    //delete
    void delete(String categoryId);

    //get all
    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get single category detail
    CategoryDto get(String categoryId);

    //search
    List<CategoryDto> searchCategories(String keyword);
}
