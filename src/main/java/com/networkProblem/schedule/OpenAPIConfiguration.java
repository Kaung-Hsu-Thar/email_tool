package com.networkProblem.schedule;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Kaung Hsu Thar");
        myContact.setEmail("kaunghsuthar0@gmail.com");

        Info information = new Info()
                .title("Network Problem Schedule System API")
                .version("1.0")
                .description("This API exposes endpoints to manage Alarms.")
                .contact(myContact);

        SecurityScheme securityScheme = new SecurityScheme()
                .name("JWT")
                .type(Type.HTTP)
                .in(In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter 'Bearer' [space] and then your token in the text input below.\n\nExample: \"Bearer eyJhbGciOiJIUzI1NiIs...\"");

        return new OpenAPI()
                .info(information)
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("JWT", securityScheme));
    }
}
