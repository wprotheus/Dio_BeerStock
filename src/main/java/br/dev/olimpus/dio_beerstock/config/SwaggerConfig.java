package br.dev.olimpus.dio_beerstock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beer Stock API")
                        .description("REST API for beer stock management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("wprotheus")
                                .url("https://github.com/wprotheus")
                                .email("wprotheus@olimpus.dev.br")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor local"));
    }
}
