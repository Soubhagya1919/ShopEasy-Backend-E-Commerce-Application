package com.soubhagya.electronic.store.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.soubhagya.electronic.store.constants.Providers;
import com.soubhagya.electronic.store.dtos.*;
import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.exceptions.BadApiRequestException;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.security.JwtHelper;
import com.soubhagya.electronic.store.services.RefreshTokenService;
import com.soubhagya.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Collections;

/**
 * The AuthenticationController class provides REST APIs for user authentication,
 * including JWT token generation, refresh token handling, and login with Google OAuth.
 * It utilizes Spring Security and JWT for authentication and authorization processes.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "AuthenticationController", description = "APIs for Authentication!!")
@SecurityRequirement(name = "scheme1")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.google.client_id}")
    private String googleClientId;

    @Value("${app.google.default.password}")
    private String googleProviderDefaultPassword;

    /**
     * Regenerates a JWT response containing a new access token and associated user information.
     *
     * @param request the request containing the refresh token needed to regenerate a new JWT token
     * @return a ResponseEntity containing a JwtResponse with the new access token, the refresh token, and user information
     */
    @PostMapping("/regenerate-token")
    public ResponseEntity<JwtResponse> regenerateResponse(@RequestBody RefreshTokenRequest request) {

        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(request.getRefreshToken());
        RefreshTokenDto verified = refreshTokenService.verifyRefreshToken(refreshTokenDto);
        UserDto user = refreshTokenService.getUser(verified);
        String jwtToken = jwtHelper.generateToken(modelMapper.map(user, User.class));

        JwtResponse response = JwtResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshTokenDto)
                .user(user)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates the user and generates a JWT token along with a refresh token.
     *
     * @param request the request containing the user's login credentials, specifically an email and a password
     * @return a ResponseEntity containing a JwtResponse, which includes the JWT token, user details, and refresh token
     */
    //method to generate token
    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        log.info("Username {}, Password {}", request.getEmail(), request.getPassword());
        this.doAuthenticate(request.getEmail(), request.getPassword());//if this statement generates exception user won't be authenticated
        //generate token
        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtHelper.generateToken(user);

        //refresh token logic
        RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        //sending the response
        JwtResponse response = JwtResponse.builder()
                .token(token)
                .user(modelMapper.map(user, UserDto.class))
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Attempts to authenticate a user using their email and password.
     *
     * @param email the email address of the user attempting to authenticate
     * @param password the password of the user attempting to authenticate
     * @throws BadCredentialsException if the authentication fails due to invalid credentials
     */
    private void doAuthenticate(String email, String password) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authentication);
        }catch(BadCredentialsException bex) {
            throw new BadCredentialsException("Invalid Username and Password !!");
        }
    }

    /**
     * Handles login with Google by verifying the provided Google ID token, extracting user details,
     * and generating a JWT for authenticated access.
     *
     * @param loginRequest the request containing the Google ID token to be verified and used for login
     * @return a ResponseEntity containing a JwtResponse with the generated JWT and user details
     * @throws BadApiRequestException if the provided Google ID token is invalid
     */
    //handle login with Google
    @PostMapping("/login-with-google")
    public ResponseEntity<JwtResponse> handleGoogleLogin(@RequestBody GoogleLoginRequest loginRequest) {
        log.info("Received Google login request with ID Token: {}", loginRequest.getIdToken());

        // Verify the Google ID Token
        GoogleIdToken googleIdToken = verifyGoogleIdToken(loginRequest.getIdToken());

        if (googleIdToken == null) {
            log.error("Invalid Google token");
            throw new BadApiRequestException("Invalid Google User");
        }

        // Extract user details from token payload
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        UserDto userDto = extractUserDetailsFromGooglePayload(payload);

        // Handle existing user or create new user
        UserDto user = handleExistingOrNewUser(userDto);

        // Authenticate the user and generate JWT
        String token = authenticateAndGenerateToken(user);

        // Return response with JWT
        JwtResponse jwtResponse = JwtResponse.builder().token(token).user(user).build();
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * Verifies a Google ID token using a GoogleIdTokenVerifier.
     *
     * @param idToken the ID token to be verified
     * @return the verified GoogleIdToken if the verification is successful
     * @throws RuntimeException if the token verification fails due to a security exception or I/O error
     */
    private GoogleIdToken verifyGoogleIdToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory())
                .setAudience(Collections.singleton(googleClientId))
                .build();

        try {
            return verifier.verify(idToken);
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google ID token", e);
            throw new RuntimeException("Token verification failed");
        }
    }

    /**
     * Extracts user details from the given Google ID token payload.
     *
     * @param payload the Google ID token payload containing user information
     * @return a UserDto object containing the extracted user details such as name, email, picture URL,
     *         and other default information related to the Google provider
     */
    private UserDto extractUserDetailsFromGooglePayload(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        log.info("User Details: Name={}, Email={}, PictureUrl={}", name, email, pictureUrl);

        return UserDto.builder()
                .name(name)
                .email(email)
                .imageName(pictureUrl)
                .password(googleProviderDefaultPassword)
                .about("User created using Google OAuth")
                .provider(Providers.GOOGLE)
                .build();
    }

    /**
     * Handles the process of checking whether a user already exists in the system or creating a new user if none is found.
     * It attempts to retrieve a user by email and verifies the authentication provider.
     * If the user does not exist, a new user is created.
     *
     * @param userDto the data transfer object containing user information, including email and provider details, pertinent for identifying or creating a user.
     * @return a UserDto object representing the existing user found or the newly created user.
     * @throws BadCredentialsException if the existing user's authentication provider does not match the one provided in userDto.
     */
    private UserDto handleExistingOrNewUser(UserDto userDto) {
        try {
            // Check if user already exists
            UserDto existingUser = userService.getUserByEmail(userDto.getEmail());
            log.info("Existing user found in the database");

            if (!existingUser.getProvider().equals(userDto.getProvider())) {
                throw new BadCredentialsException("Email is already registered. Try logging in with username and password.");
            }
            return existingUser;

        } catch (ResourceNotFoundException ex) {
            // Create a new user if not found
            log.info("No existing user found. Creating new user.");
            return userService.createUser(userDto);
        }
    }

    /**
     * Authenticates the user based on their email and a default password,
     * then generates a JWT token for the authenticated user.
     *
     * @param userDto the data transfer object containing user information required for authentication
     * @return a JWT token string for the authenticated user
     */
    private String authenticateAndGenerateToken(UserDto userDto) {
        this.doAuthenticate(userDto.getEmail(), googleProviderDefaultPassword);

        User userEntity = modelMapper.map(userDto, User.class);
        return jwtHelper.generateToken(userEntity);
    }

    /**
     * Retrieves the current authenticated user's details.
     *
     * @param principal the security principal representing the currently authenticated user
     * @return a ResponseEntity containing the UserDto of the current user and an HTTP status of OK
     */
    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        String name = principal.getName();
        return new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(name), UserDto.class), HttpStatus.OK);
    }

}
