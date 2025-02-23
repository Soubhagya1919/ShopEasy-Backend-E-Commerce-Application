package com.soubhagya.electronic.store.config;

import com.soubhagya.electronic.store.constants.AppConstants;
import com.soubhagya.electronic.store.security.JwtAuthenticationEntryPoint;
import com.soubhagya.electronic.store.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;


/**
 * Configuration class for managing application security settings.
 *
 * This class is responsible for defining the security configuration
 * for the application using Spring Security. It includes settings
 * for URL authorization, CORS configuration, CSRF protection, and
 * session management. It also sets up JWT-based authentication and
 * includes a custom JWT filter and authentication entry point.
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter filter;

    private final String[] PUBLIC_URLS = {
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs",
            "/test"
    };

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * This method sets up the HTTP security configuration using
     * Spring Security's {@link HttpSecurity}. It defines URL access
     * rules based on user roles, configures CORS settings, disables
     * CSRF protection, sets session management policies, and adds
     * custom filters.
     *
     * @param security the {@link HttpSecurity} instance used to configure security
     * @return a {@link SecurityFilterChain} that defines the security filter chain
     * @throws Exception if an error occurs while building the security configuration
     */
    //SecurityFilterChain bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        //configuration using HttpSecurity

        //urls(public or protected)
        //RBAC(urls authorization based on user roles)

        //configuring cors
        security.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource((new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration corsConfiguration = new CorsConfiguration();

                        //origins //methods
                        //corsConfiguration.addAllowedOrigin("http://localhost:4200");
                        //corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));
                        //Not recommended in production
                        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("*"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedHeaders(List.of("*"));
                        //Configure how long, in seconds, the response from a pre-flight request
                        //can be cached by clients.
                        corsConfiguration.setMaxAge(3000L);
                        return corsConfiguration;
                    }
                })));
        //disabling csrf for now
        security.csrf(AbstractHttpConfigurer::disable);

        security.authorizeHttpRequests(request -> {
            request.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(AppConstants.ROLE_ADMIN)
                    .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole(AppConstants.ROLE_ADMIN, AppConstants.ROLE_NORMAL)
                    .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                    .requestMatchers("/products/**").hasRole(AppConstants.ROLE_ADMIN)
                    .requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                    .requestMatchers(HttpMethod.POST,"/users").permitAll()
                    .requestMatchers(HttpMethod.GET,"/categories/**").permitAll()
                    .requestMatchers("/categories/**").hasRole(AppConstants.ROLE_ADMIN)
                    .requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/login-with-google", "/auth/regenerate-token").permitAll()
                    .requestMatchers("/auth/**").authenticated()
                    //new addition
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    .anyRequest().permitAll();
        });

        //What kind of security you want to use
        //security.httpBasic(Customizer.withDefaults());

        //entry point
        security.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        //session creation policy
        security.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //main -> logic for our filter to be executed before the UsernamePasswordAuthenticationFilter
        security.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }

    /**
     * Creates a bean for password encoding using the BCrypt hashing algorithm.
     *
     * The password encoder bean is used to securely hash and verify passwords within
     * the application, providing a high level of security for sensitive user information.
     *
     * @return a PasswordEncoder instance initialized with BCryptPasswordEncoder for secure password operations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and returns an {@link AuthenticationManager} bean.
     *
     * This method retrieves the {@link AuthenticationManager} from the
     * provided {@link AuthenticationConfiguration}, which is used to
     * handle authentication in the application.
     *
     * @param authenticationConfiguration the configuration from which the
     *                                    {@link AuthenticationManager} is obtained
     * @return the {@link AuthenticationManager} for handling authentication
     * @throws Exception if there's an error when obtaining the {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
