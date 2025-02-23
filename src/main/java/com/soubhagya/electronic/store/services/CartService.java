package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.AddItemToCartRequest;
import com.soubhagya.electronic.store.dtos.CartDto;

/**
 * Service interface for managing shopping carts. Provides functionality to add, remove,
 * and retrieve items from a user's cart, as well as clearing the cart entirely.
 */
public interface CartService {
    //add items to cart
    CartDto addItemToCart(String userId, AddItemToCartRequest request);

    //remove item from cart:
    void removeItemFromCart(String userId, int cartItem);

    //remove all items from cart
    void clearCart(String userId);

    CartDto getCartByUser(String userId);
}
