package com.soubhagya.electronic.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for refreshing a JWT token using a refresh token.
 *
 * This class encapsulates the refresh token needed to request a new access token from the authentication server.
 *
 * Attributes:
 * - refreshToken: A string containing the refresh token used to obtain a new access token.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
