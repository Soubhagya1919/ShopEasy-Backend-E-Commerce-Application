package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing OrderItem entities in the database.
 * Extends JpaRepository to provide CRUD operations for OrderItem entities
 * using their integer-based identifiers.
 *
 * This repository facilitates interaction with the database for operations
 * pertaining to order items in an e-commerce system.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
