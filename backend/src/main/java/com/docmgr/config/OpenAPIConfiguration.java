package com.docmgr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Document Management LLM API")
                        .description("RESTful API for LLM operations with Gemini and Ollama providers")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Document Management Service")
                                .email("admin@docmgr.local"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:3000")
                        .description("Development server"))
                .addServersItem(new Server()
                        .url("http://192.168.4.8:3000")
                        .description("Production server"));
    }
}