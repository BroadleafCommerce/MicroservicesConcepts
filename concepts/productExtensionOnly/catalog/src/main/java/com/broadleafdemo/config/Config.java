package com.broadleafdemo.config;

import org.springframework.context.annotation.Configuration;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;

import static com.broadleafcommerce.catalog.provider.RouteConstants.Persistence.CATALOG_ROUTE_PACKAGE;

@Configuration
@JpaEntityScan(basePackages = "com.broadleafdemo.domain", routePackage = CATALOG_ROUTE_PACKAGE)
public class Config {}
