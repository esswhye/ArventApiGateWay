package com.arvent.zuul.config;


import com.arvent.zuul.filter.LogPostFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {
    /*
    @Bean
    public LogFilter preFilter(){
        return new LogFilter();
    }
       */
    @Bean
    public LogPostFilter logPostFilter(){return new LogPostFilter();}

}