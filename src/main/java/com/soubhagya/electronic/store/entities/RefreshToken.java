package com.soubhagya.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a refresh token entity used in authentication and session management.
 * This class is an entity and is mapped to a database table.
 *
 * The RefreshToken class contains details about the refresh token,
 * including its unique identifier, token value, expiration date, and
 * associated user entity.
 *
 * Annotations are used to facilitate automatic generation of boilerplate code
 * such as constructors, getters, setters, and builder methods, and to define
 * entity relationships and persistence behaviors.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;//token inside RefreshToken class will be the refresh token

    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
