package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Category;
import com.soubhagya.electronic.store.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductRepository is an interface for performing CRUD operations on Product entities.
 * It extends JpaRepository to leverage Spring Data JPA's functionality for database
 * interactions, including pagination and sorting.
 *
 * This repository defines custom search methods to facilitate finding products by
 * specific attributes:
 *
 * - findByTitleContaining: Finds all products where the title contains the specified substring,
 *   supporting pagination to manage result sets efficiently.
 *
 * - findByLiveTrue: Retrieves all products that are marked as live, using pagination
 *   to control the size of the result set.
 *
 * - findByCategory: Finds products belonging to a specified category, also supporting
 *   pagination to handle potentially large results.
 *
 * These methods assist in tailoring queries to suit various business needs related to
 * product discovery, categorization, and availability.
 */
public interface ProductRepository extends JpaRepository<Product, String> {
    //search
    Page<Product> findByTitleContaining(String subTitle, Pageable pageable);

    Page<Product> findByLiveTrue(Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    //other custom finder methods
    //query methods
}
