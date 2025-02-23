# Electronic Store API

## Overview

The Electronic Store API is a RESTful service designed to manage various aspects of an electronic store. It provides functionalities for user management, order processing, and file operations. The API is built using Java and Spring Boot, leveraging JWT for security and Swagger for API documentation.

## Features

- **User Management**: Create, update, delete, and retrieve user profiles. Manage user roles and authentication through JWT.
- **Order Management**: Create, update, delete, and retrieve orders with paginated response support.
- **File Handling**: Upload and retrieve user profile images with file type restrictions.
- **Security**: Secure API endpoints with JWT authentication.
- **API Documentation**: Integrated with Swagger for easy API exploration and testing.

## Technologies Used

- Java
- Spring Boot
- JWT (JSON Web Tokens)
- Swagger/OpenAPI
- Hibernate/JPA
- Maven

## Project Structure

- `com.soubhagya.electronic.store.controller`: Contains REST controllers for managing users and orders.
- `com.soubhagya.electronic.store.dtos`: Data Transfer Objects for API communications.
- `com.soubhagya.electronic.store.entities`: JPA entities representing database tables.
- `com.soubhagya.electronic.store.services`: Service layer interfaces and implementations for business logic.
- `com.soubhagya.electronic.store.security`: JWT utility classes for token management.
- `com.soubhagya.electronic.store.config`: Configuration classes for Swagger and other settings.

## Getting Started

### Prerequisites

- Java 11+
- Maven 3.6+
- A database (e.g., MySQL, PostgreSQL) configured with the appropriate connection properties.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/electronic-store.git
   ```
2. Navigate to the project directory:
   ```bash
   cd electronic-store
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### Running the Application

1. Update the `application.properties` file with your database and JWT secret configurations.
2. Start the application:
   ```bash
   mvn spring-boot:run
   ```

### API Documentation

Access the Swagger UI for API documentation and testing at `http://localhost:8080/swagger-ui.html`.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
