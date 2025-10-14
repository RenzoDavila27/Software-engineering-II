package com.example.apiexterna.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${imdb.api.key}")
    private String apiKey;

	@Bean
	public WebClient webClient() {
		System.out.println("DEBUG: Clave de API cargada: " + apiKey); 
		return WebClient.builder()
			.baseUrl("https://imdb236.p.rapidapi.com")
			.defaultHeader("X-RapidAPI-Key", apiKey)
			.defaultHeader("X-RapidAPI-Host", "imdb236.p.rapidapi.com")
			.build();
	}
}



