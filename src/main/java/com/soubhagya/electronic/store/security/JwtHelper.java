package com.soubhagya.electronic.store.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//this class is used to perform jwt operations

//jwt generate
//fetching username from the token

/**
 * JwtHelper is a utility class for working with JSON Web Tokens (JWTs).
 * It provides methods for generating tokens, parsing claims,
 * and validating token expiration.
 *
 * Fields:
 * - TOKEN_VALIDITY: The validity period of the token in milliseconds.
 * - SECRET_KEY: The secret key used for signing JWTs.
 *
 * Methods:
 * - getUsernameFromToken: Retrieves the username from a given JWT.
 * - getClaimFromToken: Generic method to extract any claim from a JWT.
 * - getAllClaimsFromToken: Retrieves all claims from a JWT using the secret key.
 * - isTokenExpired: Checks if a JWT has expired.
 * - getExpirationDateFromToken: Retrieves the expiration date of a JWT.
 * - generateToken: Generates a JWT for a given UserDetails object.
 * - doGenerateToken: Constructs and signs a JWT with specified claims and subject.
 */
@Component
public class JwtHelper {
    //requirement

    /**
     * Defines the duration for which a JWT token remains valid, expressed in milliseconds.
     * This constant represents the time span during which the token can be used for authentication
     * before it expires and needs to be refreshed or replaced. The TOKEN_VALIDITY is set to 5 hours.
     */
    // 1. validity
    public static final long TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    /**
     * SECRET_KEY is a property injected from the application configuration
     * used as the secret key for signing and verifying JWT tokens.
     *
     * This key is crucial for the cryptographic operations carried out
     * within the application, such as generating tokens and validating
     * their integrity and authenticity.
     *
     * Maintaining the confidentiality of this key is vital for ensuring
     * the security of the JWT-based authentication mechanism.
     */
    // 2. secret key(private key)
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    //encoding the secret key

    /**
     * Retrieves the username from the provided JWT token.
     *
     * This method extracts the subject claim from the JWT, which is typically used to represent the username
     * of the authenticated user. It uses the {@code getClaimFromToken} method to fetch the subject.
     *
     * @param token the JWT token from which the username is to be extracted
     * @return the username contained within the token, or null if the extraction fails
     */
    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a given JWT token using a provided claims resolver function.
     *
     * @param token the JWT token from which the claim is to be extracted
     * @param claimsResolver a function that takes the Claims object and extracts a specific claim
     * @return the claim extracted from the token, as defined by the claimsResolver function
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves all claims contained within a specified JWT token.
     *
     * This method uses the secret key to parse the JWT and extract all claims
     * without validating the signature. It is intended to be used internally
     * when the application needs to access the information stored within the token.
     *
     * @param token the JWT token from which claims are to be extracted.
     * @return the claims contained within the specified JWT token.
     */
    //for retrieving any info from the token we need the secret key
    private Claims getAllClaimsFromToken(String token) {

//        SignatureAlgorithm hs512 = SignatureAlgorithm.HS512;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), hs512.getJcaName());
//        return (Claims)Jwts.parser().verifyWith(secretKeySpec).build().parse(token).getPayload();

        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getPayload();
    }

    /**
     * Checks whether the provided JWT token is expired.
     *
     * @param token the JWT token to be checked for expiration
     * @return true if the token is expired, false otherwise
     */
    //check if the token is expired
    public Boolean isTokenExpired(String token) {
        final Date expiration= getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Retrieves the expiration date from the specified JWT token.
     *
     * @param token the JWT token from which to extract the expiration date
     * @return the expiration date of the token, or null if the token is invalid or does not have an expiration claim
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generates a JWT token for the provided user details.
     *
     * @param userDetails The details of the user for whom the token should be generated. This should include at least the username.
     * @return A JWT token as a String, which encodes the user details and can be used for authentication.
     */
    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Generates a JWT token using the specified claims and subject.
     *
     * @param claims a map containing the claims to be set in the JWT token
     * @param subject the subject for which the token is being generated
     * @return a string representation of the generated JWT token
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))//multiply 1000 if it's not already converted to ms
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

}
