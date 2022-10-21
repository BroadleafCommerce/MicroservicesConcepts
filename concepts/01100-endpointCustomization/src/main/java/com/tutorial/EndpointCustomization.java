package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.tutorial.endpoint.ExtendedProductEndpoint;

/**
 * Setup components for the extension case.
 */
@Configuration
@ComponentScan(basePackageClasses = ExtendedProductEndpoint.class)
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class)
public class EndpointCustomization {}
