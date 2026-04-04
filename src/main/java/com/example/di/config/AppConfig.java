package com.example.di.config;

import com.example.di.annotations.Bean;
import com.example.di.annotations.Configuration;
import com.example.di.beans.Radio;

@Configuration
public class AppConfig {

    @Bean
    public Radio myRadio() {
        return new Radio();
    }
}
