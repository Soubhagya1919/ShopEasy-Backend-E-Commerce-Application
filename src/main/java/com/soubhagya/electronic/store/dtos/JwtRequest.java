package com.soubhagya.electronic.store.dtos;

import lombok.*;

/**
 * Represents a request for JWT authentication containing an email and a password.
 *
 * This class is typically used to encapsulate the user's credentials for authentication
 * purposes. It includes the email and password fields which are necessary for generating
 * a JWT token.
 *
 * Attributes:
 * - email: A string representing the user's email address used for authentication.
 * - password: A string representing the user's password associated with the email.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtRequest {
    private String email;
    private String password;
}
