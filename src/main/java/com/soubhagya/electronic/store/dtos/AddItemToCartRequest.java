package com.soubhagya.electronic.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request to add an item to a shopping cart.
 * This class encapsulates the details required to add a specific product
 * to the cart, including the product identifier and the desired quantity.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddItemToCartRequest {

    private String productId;

    private int quantity;
}
