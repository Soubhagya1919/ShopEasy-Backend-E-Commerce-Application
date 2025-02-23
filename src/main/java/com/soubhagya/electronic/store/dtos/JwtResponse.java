package com.soubhagya.electronic.store.dtos;

import lombok.*;

/**
 * Represents a response containing a JWT token, user details, and a refresh token.
 *
 * This class is typically used to encapsulate the response information after a successful
 * authentication attempt, providing the necessary tokens and user information required for
 * further interactions with the system.
 *
 * Attributes:
 * - token: A string representing the JSON Web Token (JWT) for authentication.
 * - user: A UserDto object containing user-specific details.
 * - refreshToken: A RefreshTokenDto object used to refresh the JWT token when it expires.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    UserDto user;
    //private String jwtToken;
    private RefreshTokenDto refreshToken;
}
