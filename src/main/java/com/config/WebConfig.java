package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**") // Aplica CORS para todos os endpoints
                        .allowedOrigins("http://localhost:5000") // Permite apenas esta origem
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite esses métodos
                        .allowedHeaders("*") // Permite todos os headers
                        .allowCredentials(true); // Permite credenciais, se necessário
            }
        };
        
    }
}