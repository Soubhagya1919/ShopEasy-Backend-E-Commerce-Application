package com.soubhagya.electronic.store.entities;

import lombok.*;

import jakarta.persistence.*;

/**
 * Represents an item within a customer's order in an e-commerce application.
 * Each OrderItem is associated with a specific product and references its quantity and total price.
 *
 * This class is an entity mapped to the "oder_items" table in the database.
 * It maintains relationships with the Product and Order entities.
 * The OrderItem is linked to a single Product and belongs to one Order.
 *
 * Attributes:
 * - orderItemId: A unique identifier for each order item.
 * - quantity: The number of units of the product being ordered.
 * - totalPrice: The total price for this order item, calculated as product price multiplied by quantity.
 * - product: The product associated with this order item.
 * - order: The order to which this item belongs.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "oder_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    private int quantity;

    private double totalPrice;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
