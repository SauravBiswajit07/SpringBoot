package com.ecommerce.project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean//model mapper used to convert one object type to another ...used in service impl
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
