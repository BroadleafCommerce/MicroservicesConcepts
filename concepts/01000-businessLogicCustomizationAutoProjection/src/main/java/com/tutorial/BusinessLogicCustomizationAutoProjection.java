package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.tutorial.service.ElectricCarService;


/**
 * Setup components for the extension case.
 */
@Configuration
@ComponentScan(basePackageClasses = ElectricCarService.class) // Component scan for our service
                                                              // customization
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class) // Configure before Broadleaf so we can
                                                            // override the service
public class BusinessLogicCustomizationAutoProjection {}
