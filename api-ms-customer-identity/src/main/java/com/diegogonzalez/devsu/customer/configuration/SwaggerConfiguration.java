package com.diegogonzalez.devsu.customer.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Configuration
public class SwaggerConfiguration {

    private final String apiVersion;
    private final String apiName;
    private final String apiDescription;
    private final String contactName;
    private final String contactEmail;

    public SwaggerConfiguration(
            @Value("${swagger.info.version:1.0.0}") String apiVersion,
            @Value("${swagger.info.name:API}") String apiName,
            @Value("${swagger.info.description:API Documentation}") String apiDescription,
            @Value("${swagger.info.contact.name:Developer}") String contactName,
            @Value("${swagger.info.contact.mail:contact@example.com}") String contactEmail) {
        this.apiVersion = apiVersion;
        this.apiName = apiName;
        this.apiDescription = apiDescription;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    @Bean
    public OpenAPI apiDocumentation() {
        return new OpenAPI()
                .info(createApiInfo())
                .components(createComponents());
    }

    private Info createApiInfo() {
        return new Info()
                .title(apiName)
                .version(apiVersion)
                .description(apiDescription)
                .contact(createContactInfo());
    }

    private Contact createContactInfo() {
        return new Contact()
                .name(contactName)
                .email(contactEmail);
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearer-jwt", createJwtSecurityScheme())
                .addResponses("BadRequestError", createBadRequestResponse())
                .addResponses("UnauthorizedError", createUnauthorizedResponse())
                .addResponses("NotFoundError", createNotFoundResponse())
                .addResponses("ServerError", createServerErrorResponse());
    }

    private SecurityScheme createJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Provide a JWT token to access secured endpoints");
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
                .description("Bad request due to invalid parameters");
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("Unauthorized access");
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
                .description("Resource not found");
    }

    private ApiResponse createServerErrorResponse() {
        return new ApiResponse()
                .description("Internal server error");
    }
}