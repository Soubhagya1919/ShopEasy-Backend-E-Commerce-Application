package com.soubhagya.electronic.store.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a category in an e-commerce platform. This entity is mapped to
 * the "categories" table in the database and is used to organize products.
 * Each category has a unique identifier, a title, a description, and can be
 * associated with multiple products.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id")
    private String categoryId;

    @Column(name = "category_title", length = 60, nullable = false)
    private String title;

    @Column(name = "category_desc", length = 500)
    private String description;

    private String coverImage;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    //HashSet can be used as well for efficiency

}
