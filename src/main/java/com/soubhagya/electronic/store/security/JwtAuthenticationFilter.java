package com.soubhagya.electronic.store.security;

import com.soubhagya.electronic.store.constants.AppConstants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JwtAuthenticationFilter is a custom filter that extends OncePerRequestFilter
 * to handle JWT authentication in each request. This filter intercepts HTTP requests
 * and verifies the JWT tokens present in the Authorization header.
 *
 * The class uses JwtHelper to handle JWT token operations such as extracting the username
 * and checking token validity. It also relies on UserDetailsService to load user-specific
 * data from a data source.
 *
 * Upon successful validation of the token and user details, the filter sets up a security
 * context with authentication details, allowing the request to proceed with a trusted
 * authentication object in the security context.
 *
 * The filter expects the JWT to be present in the HTTP Authorization header with the
 * "Bearer " prefix. If the token is missing, malformed, or expired, the filter logs
 * appropriate error messages without interrupting the filter chain.
 *
 * This filter is designed to be applied to each request within a web application's
 * security filter chain.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    private final UserDetailsService userDetailsService;

    /**
     * Constructs an instance of JwtAuthenticationFilter.
     *
     * @param jwtHelper An instance of JwtHelper, used for performing JWT operations such as generating tokens
     *                  and retrieving claims contained within the token.
     * @param userDetailsService An instance of UserDetailsService, used to load user-specific data, especially
     *                           during the authentication process.
     */
    public JwtAuthenticationFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Processes incoming HTTP requests and performs JWT authentication by extracting, validating,
     * and setting the authentication token in the security context if valid.
     *
     * The method first retrieves the JWT from the HTTP request header, validates it, and extracts
     * the username. If the token is valid and the user is authenticated, the relevant security
     * context is set with an authentication object containing the user's details.
     *
     * @param request the incoming HTTP request that may contain a JWT
     * @param response the HTTP response which can be used to return errors or other information
     * @param filterChain the filter chain to pass the request and response to the next entity
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException if an input or output exception occurs
     */
    //new lambda style implementation
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(AppConstants.JWT_HEADER_NAME))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .ifPresentOrElse(token -> {
                    //we have the token
                    try {
                        Optional.ofNullable(jwtHelper.getUsernameFromToken(token))
                                .ifPresent(username -> {
                                    log.info("Token username is {} ", username);
                                    if(SecurityContextHolder.getContext().getAuthentication() == null) {
                                        //load user detail from the database
                                        Optional.ofNullable(userDetailsService.loadUserByUsername(username))
                                                .filter(userDetails -> {
                                                    boolean isValidUser = username.equals(userDetails.getUsername());
                                                    boolean isTokenValid = !jwtHelper.isTokenExpired(token);
                                                    return isTokenValid && isValidUser;
                                                })
                                                .ifPresent(userDetails -> {
                                                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                                });
                                    }
                                });
                    } catch (IllegalArgumentException ex) {
                        log.error("Illegal argument while fetching the username !! {}", ex.getMessage());
                    } catch (ExpiredJwtException ex) {
                        log.error("Token validity has expired !! {}", ex.getMessage());
                    } catch (MalformedJwtException ex) {
                        log.error("Some changes has been done on the token !! Invalid Token {}", ex.getMessage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, () -> {
                    //value empty
                    logger.info("Invalid Header !! Header doesn't start with Bearer");
                });
        filterChain.doFilter(request,response);
    }

    //Old implementation
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        //code executed before each request reaches to our apis, and it will verify the token
//
//        //Authorization: Bearer token value...
//        String requestHeader = request.getHeader("Authorization");
//        log.info("Header {} ", requestHeader);
//
//        String username = null;
//        String token = null;
//
//        if(requestHeader != null && requestHeader.startsWith("Bearer")){
//            token = requestHeader.substring(7);
//
//            try {
//                username = jwtHelper.getUsernameFromToken(token);
//                log.info("Token Username: {}", username);
//            } catch (IllegalArgumentException ex) {
//                log.error("Illegal argument while fetching the username !! {}", ex.getMessage());
//            } catch (ExpiredJwtException ex) {
//                log.error("Token validity has expired !! {}", ex.getMessage());
//            } catch (MalformedJwtException ex) {
//                log.error("Some changes has been done on the token !! Invalid Token {}", ex.getMessage());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//        } else {
//            log.error("Invalid Header !! Header is not starting with Bearer");
//        }
//
//        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            //validate token
//            if(username.equals(userDetails.getUsername()) && !jwtHelper.isTokenExpired(token)) {
//                //token is valid
//                //setting authentication details inside security context
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
}
