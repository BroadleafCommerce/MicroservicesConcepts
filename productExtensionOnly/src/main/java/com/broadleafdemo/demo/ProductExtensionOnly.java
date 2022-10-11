package com.broadleafdemo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.broadleafcommerce.resource.security.SecurityEnhancer;

import java.util.Collections;

@SpringBootApplication
public class ProductExtensionOnly {

    public static void main(String[] args) {
        SpringApplication.run(ProductExtensionOnly.class, args);
    }

}
