package com.broadleafcommerce.microservices.tutorial;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.broadleafcommerce.resource.security.SecurityEnhancer;
import java.util.Collections;

@Configuration
public class BasicConfiguration {

    @Bean
    @ConditionalOnProperty("tutorial.cors.filter.enabled")
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Don't do this in production, use a proper list of allowed origins
        config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    @ConditionalOnProperty("broadleafdemo.actuator.anonymous")
    public SecurityEnhancer actuatorAnonymous() {
        return http -> {
            http.authorizeRequests().antMatchers("/actuator/*").permitAll();
            http.authorizeRequests().antMatchers("/actuator/*/*").permitAll();
        };
    }

}
