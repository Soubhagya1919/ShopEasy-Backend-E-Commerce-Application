package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Cart;
import com.soubhagya.electronic.store.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing CartItem entities.
 * Extends JpaRepository to provide CRUD operations for CartItem entities in the database.
 *
 * This repository also includes a custom method to delete all CartItem records linked to a specific Cart.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart = :cart")
    void deleteByCart(Cart cart);
}
