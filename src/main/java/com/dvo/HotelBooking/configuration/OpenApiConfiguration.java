package com.dvo.HotelBooking.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPIDescription() {
        Server localhostServer = new Server();
        localhostServer.setUrl("http://localhost:8080");
        localhostServer.setDescription("Local environment");
        Contact contact = new Contact();
        contact.setName("Kadzaeva Viktoriya");
        contact.setEmail("ms.kadzaeva@mail.ru");
        License license = new License().name("GNU AGPLv3").url("https://www.gnu.org/licenses/agpl-3.0.en.html");
        Info info = new Info()
                .title("Hotel booking service")
                .version("1.0")
                .contact(contact)
                .description("API for hotel booking service")
                .termsOfService("http://example.term.url")
                .license(license);
        return new OpenAPI().info(info).servers(List.of(localhostServer));

    }
}
