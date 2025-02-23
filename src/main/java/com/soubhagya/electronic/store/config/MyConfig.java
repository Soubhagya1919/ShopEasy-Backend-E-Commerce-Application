package com.soubhagya.electronic.store.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up application-wide beans.
 *
 * This class is responsible for defining beans that are used
 * throughout the application. It includes a method to create
 * and configure a ModelMapper bean.
 */
@Configuration
public class MyConfig {

    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
}
