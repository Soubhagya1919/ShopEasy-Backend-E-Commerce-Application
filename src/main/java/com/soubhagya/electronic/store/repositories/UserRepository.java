package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide CRUD operations on User.
 */
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    //frame a query containing Like clause
    List<User> findByNameContaining(String keywords);
}
