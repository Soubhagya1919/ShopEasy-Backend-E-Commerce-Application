package com.soubhagya.electronic.store.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Swagger and OpenAPI documentation.
 *
 * This class uses Springdoc and OpenAPI annotations to define the configuration
 * for the Swagger interface for the Electronic Store API. It specifies the
 * security scheme, general API information, contact details, license, and external
 * documentation links for the API.
 *
 * The security scheme is configured using a Bearer token with JWT format.
 * The class annotations provide metadata for the OpenAPI specification, such as
 * title, description, version, contact information, and external documentation.
 */
@Configuration
@SecurityScheme(
        name = "scheme1",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Electronic Store API",
                description = "This is backed of electronic store developed by Soubhagya Mohapatra",
                version = "1.0v",
                contact = @Contact(
                        name = "Soubhagya Mohapatra",
                        email = "soubhagyamohapatra@bbsr@gmail.com",
                        url = "https://comingsoon.com"
                ),
                license = @License(
                        name = "OPEN Licence",
                        url = "https://comingsoon.com"

                )
        )
        , externalDocs = @ExternalDocumentation(
                description = "This is external docs",
                url = "https://comingsoon.com"
        )
)
public class SwaggerConfig {

//    @Bean
//    public OpenAPI openAPI() {
//        String schemeName = "bearerScheme";
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement()
//                        .addList(schemeName)
//                )
//                .components(new Components()
//                        .addSecuritySchemes(schemeName, new SecurityScheme()
//                                .name(schemeName)
//                                .type(SecurityScheme.Type.HTTP)
//                                .bearerFormat("JWT")
//                                .scheme("bearer")
//                        )
//                )
//                .info(new Info()
//                        .title("Electronic Store API")
//                        .description("This is electronic store project api developed by Soubhagya")
//                        .version("1.0")
//                        .contact(new Contact().name("Soubhagya").email("soubhagyamohapatra.bbsr@gmail.com").url("soubhagya.com"))
//                        .license(new License().name("Apache"))
//
//                ).externalDocs(new ExternalDocumentation().url("soubhagya.com").description("this is external url"))
//                ;
//    }
}
