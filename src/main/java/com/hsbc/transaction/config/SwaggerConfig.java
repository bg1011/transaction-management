package com.hsbc.transaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * This class configures the API documentation for the Bank Transaction System,
 * including server information, API details, and contact information.
 *
 * <p>The configuration includes:
 * <ul>
 *     <li>Server environments (development and production)</li>
 *     <li>API information and versioning</li>
 *     <li>Contact information for technical support</li>
 *     <li>License information</li>
 * </ul>
 * </p>
 *
 * @author HSBC Development Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Flag indicating whether Swagger documentation is enabled.
     * Default value is true.
     */
    @Value("${swagger.enabled:true}")
    private boolean swaggerEnabled;

    /**
     * The server port number.
     * Default value is 8080.
     */
    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Creates and configures the OpenAPI documentation.
     * This method sets up the API documentation with server information,
     * API details, and contact information.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Development Environment"),
                        new Server().url("https://api.bank.com").description("Production Environment")
                ))
                .info(new Info()
                        .title("Bank Transaction System API Documentation")
                        .version("1.0")
                        .description("REST API documentation for the Bank Transaction System")
                        .contact(new Contact()
                                .name("Technical Support")
                                .email("tech@bank.com")
                                .url("https://tech.bank.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .termsOfService("https://bank.com/terms"));
    }
}
