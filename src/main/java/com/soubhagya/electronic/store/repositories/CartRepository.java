package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Cart;
import com.soubhagya.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Cart entities.
 * Extends JpaRepository to provide basic JPA functionalities.
 */
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUser(User user);
}
