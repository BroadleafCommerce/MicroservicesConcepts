package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.tutorial.endpoint.MyAutoCoProductEndpoint;



/**
 * Setup components for the extension case.
 */
@Configuration
@ComponentScan(basePackageClasses = MyAutoCoProductEndpoint.class) // scan for our customized
                                                                   // endpoint
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class) // configure before Broadleaf so we can
                                                            // override the endpoint
public class EndpointCustomizationAutoProjection {}
