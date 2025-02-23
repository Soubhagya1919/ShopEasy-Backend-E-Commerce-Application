package com.soubhagya.electronic.store.dtos;

import lombok.*;

/**
 * Represents a data transfer object for an item in a shopping cart.
 * This class encapsulates the details about a particular cart item,
 * including its unique identifier, the product details, the quantity of
 * the product chosen, and the total price for this quantity of the product.
 *
 * Attributes:
 * - cartItemId: An integer representing the unique identifier for the cart item.
 * - product: An instance of ProductDto representing the product added to the cart.
 * - quantity: An integer representing the quantity of the product in the cart.
 * - totalPrice: A double representing the total price for the given quantity of the product.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartItemDto {

    private int cartItemId;

    private ProductDto product;

    private int quantity;

    private double totalPrice;

}
