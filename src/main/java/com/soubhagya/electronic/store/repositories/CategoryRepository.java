package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing Category entities in the database.
 * Extends JpaRepository to provide basic CRUD operations and includes custom
 * query methods for more specific retrieval tasks.
 */
public interface CategoryRepository extends JpaRepository<Category, String> {

    // Custom query to search categories by title or description
    @Query("SELECT c FROM Category c WHERE c.title LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Category> searchByKeyword(String keyword);
}
