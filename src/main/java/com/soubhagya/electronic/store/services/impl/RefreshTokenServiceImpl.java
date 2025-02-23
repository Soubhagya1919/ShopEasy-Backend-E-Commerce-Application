package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.dtos.RefreshTokenDto;
import com.soubhagya.electronic.store.dtos.UserDto;
import com.soubhagya.electronic.store.entities.RefreshToken;
import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.repositories.RefreshTokenRepository;
import com.soubhagya.electronic.store.repositories.UserRepository;
import com.soubhagya.electronic.store.services.RefreshTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * This class implements the RefreshTokenService interface and provides
 * functionality for managing refresh tokens in the application. The service
 * interacts with the UserRepository and RefreshTokenRepository to perform
 * operations related to refresh tokens.
 *
 * This service includes methods to create, find, verify refresh tokens,
 * and retrieve the associated user based on a given refresh token.
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final ModelMapper mapper;

    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mapper = mapper;
    }

    /**
     * Creates a new refresh token for the given username. If a refresh token already exists for the user,
     * it updates the existing token with a new value and expiry date. The token is then saved to the repository.
     *
     * @param username the email address of the user for whom the refresh token is to be created
     * @return a DTO representation of the newly created or updated refresh token
     * @throws ResourceNotFoundException if the user with the provided username is not found
     */
    @Override
    public RefreshTokenDto createRefreshToken(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found!!"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);

        if(refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60))
                    .build();
        } else {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60));
        }

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return this.mapper.map(savedToken, RefreshTokenDto.class);
    }

    /**
     * Finds and returns a RefreshTokenDto object based on the provided token.
     *
     * @param token the token string to search for in the repository
     * @return a RefreshTokenDto object representing the refresh token with the specified token
     * @throws ResourceNotFoundException if no refresh token with the given token is found in the database
     */
    @Override
    public RefreshTokenDto findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found in database"));
        return this.mapper.map(refreshToken, RefreshTokenDto.class);
    }

    /**
     * Verifies the given refresh token to ensure it is still valid.
     * If the token has expired, it is deleted from the repository and a
     * RuntimeException is thrown.
     *
     * @param token the RefreshTokenDto containing the token to be verified
     * @return the same RefreshTokenDto if the token is valid and has not expired
     * @throws RuntimeException if the token has expired
     */
    @Override
    public RefreshTokenDto verifyRefreshToken(RefreshTokenDto token) {
        var refreshToken = mapper.map(token, RefreshToken.class);

        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired!!");
        }
        return token;
    }

    /**
     * Retrieves the user information associated with the specified refresh token.
     *
     * @param dto the DTO containing the refresh token for which the user information is to be retrieved
     * @return a UserDto object containing the user details mapped from the associated user entity
     * @throws ResourceNotFoundException if the refresh token is not found in the repository
     */
    @Override
    public UserDto getUser(RefreshTokenDto dto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(dto.getToken()).orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        User user = refreshToken.getUser();
        return mapper.map(user, UserDto.class);
    }
}
