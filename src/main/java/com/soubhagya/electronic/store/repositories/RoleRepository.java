package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RoleRepository is an interface for performing CRUD operations on Role entities.
 * It extends JpaRepository to provide basic JPA functionalities for Role management
 * in the database.
 *
 * This repository includes a custom method:
 * - findByName: Retrieves an Optional containing the Role entity with the specified name,
 *   if it exists in the database.
 */
public interface RoleRepository extends JpaRepository <Role, String>{
    Optional<Role> findByName(String name);
}
