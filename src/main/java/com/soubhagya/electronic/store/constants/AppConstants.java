package com.soubhagya.electronic.store.constants;

/**
 * The AppConstants class holds constant values used throughout the application.
 *
 * This class defines constants for user roles and HTTP header names that are
 * commonly used for authentication and authorization purposes.
 *
 * Constants:
 * - ROLE_ADMIN: Represents the admin role in the application.
 * - ROLE_NORMAL: Represents the normal user role in the application.
 * - JWT_HEADER_NAME: Represents the HTTP header name used to pass JWT tokens.
 */
public class AppConstants {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_NORMAL = "NORMAL";
    public static final String JWT_HEADER_NAME = "Authorization";
}
