package com.kob.matchingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    /**
     * Communication tool between two spring boot apps
     * */
    @Bean
    public RestTemplate getResTemplate(){
        return new RestTemplate();
    }
}
