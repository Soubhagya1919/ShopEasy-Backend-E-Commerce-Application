package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Order;
import com.soubhagya.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * OrderRepository is an interface for managing Order entities. It extends the JpaRepository
 * interface provided by Spring Data JPA, allowing the use of various CRUD operations on
 * Order entities without requiring explicit implementation.
 *
 * The OrderRepository interface includes a custom query method:
 * - findByUser(User user): Retrieves a list of orders associated with a given User.
 *
 * This interface enables applications to perform database operations related to
 * the Order entity in a simple and efficient manner.
 */
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUser(User user);
}
