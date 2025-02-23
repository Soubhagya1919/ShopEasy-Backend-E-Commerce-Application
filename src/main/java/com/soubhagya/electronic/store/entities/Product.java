package com.soubhagya.electronic.store.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a product in an e-commerce platform. This entity is mapped
 * to the "products" table in the database and contains details about
 * the product including its title, description, pricing, and stock
 * information.
 *
 * The Product class also establishes a relationship to a Category,
 * which indicates the type or classification under which the product falls.
 *
 * Attributes of this class include unique product identification,
 * metadata about the product such as when it was added,
 * its availability status, and any associated images.
 *
 * The class uses various annotations to define its mapping
 * configurations such as entity relationships, column specifications,
 * and table associations.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    private String productId;

    private String title;

    @Column(length = 10000)
    private String description;

    private double price;

    private double discountedPrice;

    private int quantity;

    private Date addedDate;

    private boolean live;

    private boolean stock;

    private String productImageName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    //warrantyPeriod, brandName, imageName, productRating(eg. 4.5)
}
