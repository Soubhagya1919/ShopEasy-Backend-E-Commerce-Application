package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.dtos.CategoryDto;
import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.entities.Category;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.helper.Helper;
import com.soubhagya.electronic.store.repositories.CategoryRepository;
import com.soubhagya.electronic.store.services.CategoryService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${category.image.path}")
    private String imagePath;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        //creating category randomly
        String categoryId = java.util.UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);

        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        //get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));

        //update category details
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);

        //returning dto object
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {
        //get category of given id
        Category category = categoryRepository.
                findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));

        String fullPath = imagePath + category.getCoverImage();

        Path path = Paths.get(fullPath);
        try {
            // Delete the user's profile image
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Category image deleted: {}", fullPath);
            } else {
                logger.warn("Category image not found: {}", fullPath);
            }
        } catch (IOException ex) {
            logger.error("Error occurred while deleting category image: {}", fullPath, ex);
            throw new RuntimeException("Failed to delete category image", ex);
        }

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {

        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number must be >= 0 and page size must be > 0");
        }

        Sort sort =(sortDir.equalsIgnoreCase("desc"))
                ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        return Helper.getPageableResponse(page, CategoryDto.class);
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.
                findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));

        return modelMapper.map(category, CategoryDto.class);
    }

    //implementing search functionality
    @Override
    public List<CategoryDto> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.searchByKeyword(keyword);
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }
}
