/*
package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RestServiceCorsApplication implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/login")  // Hier den Pfad angeben, der CORS erlaubt sein soll
                .allowedOrigins("http://localhost:3000")  // Erlaubte Ursprünge (Origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Erlaubte HTTP-Methoden
                .allowedHeaders("*");  // Erlaubte Header
        registry.addMapping("/api/register")  // Hier den Pfad angeben, der CORS erlaubt sein soll
                .allowedOrigins("http://localhost:3000")  // Erlaubte Ursprünge (Origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Erlaubte HTTP-Methoden
                .allowedHeaders("*");  // Erlaubte Header
    }

}
*/
