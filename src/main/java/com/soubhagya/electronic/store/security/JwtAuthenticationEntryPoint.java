package com.soubhagya.electronic.store.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of AuthenticationEntryPoint that handles unauthorized access attempts.
 *
 * This component is triggered whenever there is an attempt to access a secured REST endpoint
 * without authentication. It responds with an HTTP 401 status code and a message indicating
 * the access denial.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Commences an authentication scheme.
     *
     * <p>This method is invoked when an unauthenticated user requests a
     * secured HTTP resource. It sets the HTTP response status to Unauthorized (401)
     * and writes an "Access Denied" message along with the exception message to the response.</p>
     *
     * @param request the HTTP request for which this authentication exception was triggered
     * @param response the HTTP response to return to the client
     * @param authException the exception which caused the invocation
     * @throws IOException if an input or output exception occurs while writing to the response
     * @throws ServletException if the request could not be handled
     */
    //method will be executed when an exception is thrown due to an unauthenticated user trying to access protected resource
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.print("Access Denied " + authException.getMessage());
    }
}
