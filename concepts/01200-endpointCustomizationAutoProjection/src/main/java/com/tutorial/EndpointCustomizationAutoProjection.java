package com.tutorial;

import org.broadleafcommerce.frameworkmapping.FrameworkMappingHandlerMapping;
import org.broadleafcommerce.frameworkmapping.autoconfigure.FrameworkMappingAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.tutorial.endpoint.ElectricCarEndpoint;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import liquibase.pro.packaged.W;

/**
 * Setup components for the extension case.
 */
@Configuration
@ComponentScan(basePackageClasses = ElectricCarEndpoint.class)
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class)
public class EndpointCustomizationAutoProjection {}
