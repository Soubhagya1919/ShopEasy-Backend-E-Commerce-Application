package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.util.Date;

/**
 * Represents a data transfer object for a product in an electronic store inventory system.
 * This class encapsulates the core properties of a product, including identification,
 * description, pricing, quantity, status, and associated category details.
 *
 * Attributes:
 * - productId: A unique identifier for the product.
 * - title: The name of the product.
 * - description: A detailed description of the product.
 * - price: The original price of the product before any discounts.
 * - discountedPrice: The price of the product after applying any discounts.
 * - quantity: The available stock quantity of the product.
 * - addedDate: The date when the product was added to the inventory.
 * - live: A boolean indicating whether the product is available for sale.
 * - stock: A boolean indicating whether the product is in stock.
 * - productImageName: The filename of the product's image.
 * - category: A reference to the CategoryDto representing the product's category.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductDto {

    private String productId;

    private String title;

    private String description;

    private double price;

    private double discountedPrice;

    private int quantity;

    private Date addedDate;

    private boolean live;

    private boolean stock;

    private String productImageName;

    private CategoryDto category;
}
