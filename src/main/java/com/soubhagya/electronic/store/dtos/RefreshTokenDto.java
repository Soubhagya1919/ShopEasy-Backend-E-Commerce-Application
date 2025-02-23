package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.time.Instant;

/**
 * Represents a data transfer object for a refresh token.
 * This class is used to transfer information related to refresh tokens
 * which are typically used in authentication and authorization mechanisms
 * to obtain new access tokens.
 *
 * The class contains the following attributes:
 * - id: An integer that serves as the unique identifier for the refresh token.
 * - token: A string representing the refresh token value.
 * - expiryDate: An Instant object indicating the expiration date and time of the refresh token.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenDto {
    private int id;

    private String token;//token inside RefreshToken class will be the refresh token

    private Instant expiryDate;

}
