package com.soubhagya.electronic.store.entities;

import lombok.*;

import jakarta.persistence.*;

/**
 * Represents an item in a shopping cart in an e-commerce application.
 * This class is an entity and is mapped to a database table named "cart_items".
 *
 * Each CartItem corresponds to a specific product and quantity, and it maintains
 * the total price for the specified quantity of the product. It is associated
 * with a specific cart.
 *
 * An item has a unique identifier `cartItemId` generated automatically.
 *
 * The relationship with `Product` is a one-to-one mapping, indicating that each
 * cart item refers to a single product.
 *
 * The relationship with `Cart` is a many-to-one mapping, which allows multiple
 * cart items to belong to a single cart.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private double totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
