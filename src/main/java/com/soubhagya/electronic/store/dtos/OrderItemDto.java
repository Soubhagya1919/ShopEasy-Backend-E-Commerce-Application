package com.soubhagya.electronic.store.dtos;

import lombok.*;

/**
 * Represents a data transfer object for an order item.
 * This class encapsulates the details about a particular item in an order,
 * including the identifier for the order item, the quantity of the product,
 * the total price for the quantity purchased, and the associated product details.
 *
 * Attributes:
 * - orderItemId: An integer representing the unique identifier for the order item.
 * - quantity: An integer specifying the quantity of the product ordered.
 * - totalPrice: A double indicating the total price for the quantity of the product ordered.
 * - product: An instance of ProductDto representing the details of the product in the order.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderItemDto {

    private int orderItemId;

    private int quantity;

    private double totalPrice;

    private ProductDto product;

}
