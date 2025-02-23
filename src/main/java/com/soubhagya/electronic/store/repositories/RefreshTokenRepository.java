package com.soubhagya.electronic.store.repositories;

import com.soubhagya.electronic.store.entities.RefreshToken;
import com.soubhagya.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing RefreshToken entities.
 * Extends JpaRepository to provide standard CRUD operations and
 * includes custom methods for retrieval based on token attributes.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
