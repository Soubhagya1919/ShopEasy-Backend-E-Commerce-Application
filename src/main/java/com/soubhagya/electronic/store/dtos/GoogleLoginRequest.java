package com.soubhagya.electronic.store.dtos;

import lombok.Data;

/**
 * Represents a request object for Google login.
 * This class contains the necessary information needed
 * to authenticate a user via Google OAuth2.0 using an ID token.
 *
 * Attributes:
 * - idToken: A string representing the ID token issued by Google
 *   after user authentication.
 */
@Data
public class GoogleLoginRequest {
    private String idToken;
}
