package com.example.wsa_mes_library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WSA MES Library API")
                        .description("도서관 관리 시스템 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WSA MES Library Team")
                                .email("whdg1234@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("https://api.wsa-mes-library.com").description("Production server")
                ));
    }
}
