package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.RefreshTokenDto;
import com.soubhagya.electronic.store.dtos.UserDto;

/**
 * RefreshTokenService is an interface defining operations related to the
 * management of refresh tokens. Refresh tokens are used in the context of
 * authentication to obtain new access tokens without requiring the user to
 * re-authenticate.
 *
 * This interface provides methods for:
 * - Creating a new refresh token for a specified user.
 * - Retrieving a refresh token based on its token value.
 * - Verifying the validity of a refresh token.
 * - Retrieving the user associated with a given refresh token.
 */
public interface RefreshTokenService {
    //create token
    RefreshTokenDto createRefreshToken(String username);

    //find by token
    RefreshTokenDto findByToken(String token);

    //verify token
    RefreshTokenDto verifyRefreshToken(RefreshTokenDto token);

    UserDto getUser(RefreshTokenDto dto);
}

