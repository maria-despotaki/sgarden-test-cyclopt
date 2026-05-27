package com.sgarden.config;

import com.sgarden.model.OrderStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class WebConfig {

    @Bean
    public Converter<String, OrderStatus> stringToOrderStatusConverter() {
        return source -> OrderStatus.valueOf(source.trim().toUpperCase());
    }
}
