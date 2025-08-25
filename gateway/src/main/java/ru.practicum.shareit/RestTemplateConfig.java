package ru.practicum.shareit;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Создаем фабрику на основе Apache HttpComponents Client
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        // Настраиваем таймауты (опционально, но рекомендуется)
        factory.setConnectTimeout(5000);       // Таймаут на установку соединения
        factory.setConnectionRequestTimeout(5000); // Таймаут на получение соединения из пула

        return builder
                .requestFactory(() -> factory) // Устанавливаем нашу фабрику
                .build();
    }
}