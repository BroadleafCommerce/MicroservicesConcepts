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
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class)
@ComponentScan(basePackageClasses = ElectricCarService.class)
public class BusinessLogicCustomization {}
